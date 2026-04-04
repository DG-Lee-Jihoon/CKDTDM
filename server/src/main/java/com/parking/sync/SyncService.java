package com.parking.sync;

import com.parking.model.SyncEvent;
import com.parking.model.Vehicle;
import com.parking.repository.VehicleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SyncService {

    private static final Logger log = LoggerFactory.getLogger(SyncService.class);

    private final VehicleRepository vehicleRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final RestTemplate restTemplate;

    public SyncService(VehicleRepository vehicleRepository,
                       SimpMessagingTemplate messagingTemplate,
                       RestTemplate restTemplate) {
        this.vehicleRepository = vehicleRepository;
        this.messagingTemplate = messagingTemplate;
        this.restTemplate = restTemplate;
    }

    @Value("${server.id}")
    private int serverId;

    @Value("#{'${sync.peer.urls}'.split(',')}")
    private List<String> peerUrls;

    @Value("${sync.timeout.ms:5000}")
    private long syncTimeoutMs;

    // Lưu trạng thái lock tạm thời: eventId -> vehicle tạm
    private final Map<String, Vehicle> tempStore = new ConcurrentHashMap<>();
    // Theo dõi các peer đã ACK: eventId -> set(serverId đã confirm)
    private final Map<String, Set<Integer>> ackStore = new ConcurrentHashMap<>();

    // =========================================================
    // KHỞI ĐỘNG: Server này là coordinator (initiator)
    // =========================================================
    @Async
    public void initiateSync(Vehicle vehicle, String operation) {
        String eventId = UUID.randomUUID().toString();
        log.info("[SYNC] Bắt đầu đồng bộ - EventId: {}, Op: {}", eventId, operation);

        // === PHA 1: LOCKED ===
        SyncEvent lockEvent = SyncEvent.builder()
                .eventId(eventId)
                .phase(SyncEvent.Phase.LOCKED)
                .fromServerId(serverId)
                .vehicle(vehicle)
                .operation(operation)
                .timestamp(System.currentTimeMillis())
                .message("Khóa bản ghi, chuẩn bị đồng bộ")
                .build();

        broadcastToPeers("/api/sync/receive", lockEvent);
        broadcastToClients(lockEvent);
        log.info("[SYNC][LOCKED] EventId: {}", eventId);

        // === PHA 2: TEMPED ===
        tempStore.put(eventId, vehicle);
        ackStore.put(eventId, ConcurrentHashMap.newKeySet());

        SyncEvent tempEvent = SyncEvent.builder()
                .eventId(eventId)
                .phase(SyncEvent.Phase.TEMPED)
                .fromServerId(serverId)
                .vehicle(vehicle)
                .operation(operation)
                .timestamp(System.currentTimeMillis())
                .message("Ghi tạm thành công")
                .build();

        broadcastToPeers("/api/sync/receive", tempEvent);
        broadcastToClients(tempEvent);
        log.info("[SYNC][TEMPED] EventId: {}", eventId);

        // Chờ ACK từ peers (đơn giản hóa: chờ fixed timeout)
        try { Thread.sleep(500); } catch (InterruptedException ignored) {}

        // === PHA 3: UPDATED ===
        try {
            vehicle.setUpdatedByServer(serverId);
            applyToDatabase(vehicle, operation);

            SyncEvent updateEvent = SyncEvent.builder()
                    .eventId(eventId)
                    .phase(SyncEvent.Phase.UPDATED)
                    .fromServerId(serverId)
                    .vehicle(vehicle)
                    .operation(operation)
                    .timestamp(System.currentTimeMillis())
                    .message("Đã ghi vào DB thành công")
                    .build();

            broadcastToPeers("/api/sync/receive", updateEvent);
            broadcastToClients(updateEvent);
            log.info("[SYNC][UPDATED] EventId: {}", eventId);

        } catch (Exception e) {
            log.error("[SYNC][UPDATED] Lỗi ghi DB: {}", e.getMessage());
            rollbackPeers(eventId, vehicle, operation);
            return;
        }

        // === PHA 4: SYNCED ===
        tempStore.remove(eventId);
        ackStore.remove(eventId);

        SyncEvent syncedEvent = SyncEvent.builder()
                .eventId(eventId)
                .phase(SyncEvent.Phase.SYNCED)
                .fromServerId(serverId)
                .vehicle(vehicle)
                .operation(operation)
                .timestamp(System.currentTimeMillis())
                .message("Đồng bộ hoàn tất trên tất cả server")
                .build();

        broadcastToPeers("/api/sync/receive", syncedEvent);
        broadcastToClients(syncedEvent);
        log.info("[SYNC][SYNCED] EventId: {} hoàn tất!", eventId);
    }

    // =========================================================
    // NHẬN: Server này là replica (nhận từ coordinator)
    // =========================================================
    public void receiveSync(SyncEvent event) {
        log.info("[SYNC][RECV] Nhận pha {} từ Server {}, EventId: {}",
                event.getPhase(), event.getFromServerId(), event.getEventId());

        switch (event.getPhase()) {
            case LOCKED -> {
                // Ghi nhận lock, chuẩn bị
                log.info("[SYNC][RECV][LOCKED] Chuẩn bị nhận dữ liệu...");
                broadcastToClients(event);
            }
            case TEMPED -> {
                // Ghi tạm
                tempStore.put(event.getEventId(), event.getVehicle());
                log.info("[SYNC][RECV][TEMPED] Đã lưu tạm EventId: {}", event.getEventId());
                broadcastToClients(event);
            }
            case UPDATED -> {
                // Ghi chính thức
                try {
                    Vehicle v = event.getVehicle();
                    v.setUpdatedByServer(event.getFromServerId());
                    applyToDatabase(v, event.getOperation());
                    tempStore.remove(event.getEventId());
                    log.info("[SYNC][RECV][UPDATED] Ghi DB thành công");
                } catch (Exception e) {
                    log.error("[SYNC][RECV][UPDATED] Lỗi: {}", e.getMessage());
                }
                broadcastToClients(event);
            }
            case SYNCED -> {
                // Xác nhận hoàn tất
                tempStore.remove(event.getEventId());
                log.info("[SYNC][RECV][SYNCED] Xác nhận xong EventId: {}", event.getEventId());
                broadcastToClients(event);
            }
        }
    }

    // =========================================================
    // HELPER
    // =========================================================
    private void applyToDatabase(Vehicle vehicle, String operation) {
        switch (operation) {
            case "CREATE" -> {
                if (!vehicleRepository.existsByBienSo(vehicle.getBienSo())) {
                    // Kiểm tra ô khu+lô đã có xe CO_XE chưa
                    if (vehicle.getTrangThai() == Vehicle.TrangThai.CO_XE &&
                        vehicleRepository.existsByKhuAndLoAndTrangThai(
                            vehicle.getKhu(), vehicle.getLo(), Vehicle.TrangThai.CO_XE)) {
                        log.warn("[SYNC] Ô {}-{} đã có xe, bỏ qua CREATE", vehicle.getKhu(), vehicle.getLo());
                        return;
                    }
                    vehicleRepository.save(vehicle);
                }
            }
            case "UPDATE" -> {
                // Tìm xe theo bienSo (ID có thể khác nhau giữa các máy)
                vehicleRepository.findByBienSo(vehicle.getBienSo()).ifPresentOrElse(existing -> {
                    if (vehicle.getTrangThai() == Vehicle.TrangThai.CO_XE &&
                        vehicleRepository.existsByKhuAndLoAndTrangThaiAndIdNot(
                            vehicle.getKhu(), vehicle.getLo(), Vehicle.TrangThai.CO_XE, existing.getId())) {
                        log.warn("[SYNC] Ô {}-{} đã có xe khác, bỏ qua UPDATE", vehicle.getKhu(), vehicle.getLo());
                        return;
                    }
                    existing.setHangXe(vehicle.getHangXe());
                    existing.setKhu(vehicle.getKhu());
                    existing.setLo(vehicle.getLo());
                    existing.setTrangThai(vehicle.getTrangThai());
                    existing.setGhiChu(vehicle.getGhiChu());
                    existing.setUpdatedByServer(vehicle.getUpdatedByServer());
                    vehicleRepository.save(existing);
                    log.info("[SYNC] UPDATE thành công bienSo: {}", vehicle.getBienSo());
                }, () -> log.warn("[SYNC] Không tìm thấy xe bienSo: {} để UPDATE", vehicle.getBienSo()));
            }
            case "DELETE" -> vehicleRepository.findByBienSo(vehicle.getBienSo())
                    .ifPresent(v -> vehicleRepository.deleteById(v.getId()));
        }
    }

    private void broadcastToPeers(String path, SyncEvent event) {
        for (String peerUrl : peerUrls) {
            try {
                restTemplate.postForEntity(peerUrl + path, event, Void.class);
            } catch (Exception e) {
                log.warn("[SYNC] Không thể kết nối peer {}: {}", peerUrl, e.getMessage());
            }
        }
    }

    private void rollbackPeers(String eventId, Vehicle vehicle, String operation) {
        log.warn("[SYNC] Rollback EventId: {}", eventId);
        SyncEvent rollback = SyncEvent.builder()
                .eventId(eventId)
                .phase(SyncEvent.Phase.LOCKED) // dùng lại LOCKED để báo abort
                .fromServerId(serverId)
                .vehicle(vehicle)
                .operation("ROLLBACK")
                .timestamp(System.currentTimeMillis())
                .message("Rollback do lỗi ghi DB")
                .build();
        broadcastToPeers("/api/sync/receive", rollback);
        broadcastToClients(rollback);
        tempStore.remove(eventId);
        ackStore.remove(eventId);
    }

    // Gửi real-time tới client qua WebSocket
    private void broadcastToClients(SyncEvent event) {
        messagingTemplate.convertAndSend("/topic/sync", event);
        messagingTemplate.convertAndSend("/topic/vehicles", event);
    }
}
