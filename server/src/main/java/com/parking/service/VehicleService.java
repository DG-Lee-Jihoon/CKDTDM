package com.parking.service;

import com.parking.model.Vehicle;
import com.parking.repository.VehicleRepository;
import com.parking.sync.SyncService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class VehicleService {

    private static final Logger log = LoggerFactory.getLogger(VehicleService.class);
    private final VehicleRepository vehicleRepository;
    private final SyncService syncService;

    public VehicleService(VehicleRepository vehicleRepository, SyncService syncService) {
        this.vehicleRepository = vehicleRepository;
        this.syncService = syncService;
    }

    public List<Vehicle> getAll() {
        return vehicleRepository.findAll();
    }

    public Optional<Vehicle> getById(Long id) {
        return vehicleRepository.findById(id);
    }

    public Optional<Vehicle> getByBienSo(String bienSo) {
        return vehicleRepository.findByBienSo(bienSo);
    }

    public List<Vehicle> getByKhu(String khu) {
        return vehicleRepository.findByKhu(khu);
    }

    public List<String> getAllKhu() {
        return vehicleRepository.findDistinctKhu();
    }

    @Transactional
    public Vehicle create(Vehicle vehicle) {
        if (vehicleRepository.existsByBienSo(vehicle.getBienSo())) {
            throw new IllegalArgumentException("Biển số " + vehicle.getBienSo() + " đã tồn tại");
        }
        // Kiểm tra ô khu+lô đã có xe đang đỗ chưa
        if (vehicle.getTrangThai() == Vehicle.TrangThai.CO_XE &&
            vehicleRepository.existsByKhuAndLoAndTrangThai(vehicle.getKhu(), vehicle.getLo(), Vehicle.TrangThai.CO_XE)) {
            throw new IllegalArgumentException("Ô " + vehicle.getKhu() + "-" + vehicle.getLo() + " đã có xe đang đỗ");
        }
        Vehicle saved = vehicleRepository.save(vehicle);
        syncService.initiateSync(saved, "CREATE");
        return saved;
    }

    @Transactional
    public Vehicle update(Long id, Vehicle updated) {
        Vehicle existing = vehicleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy xe ID: " + id));

        // Kiểm tra ô khu+lô đã có xe khác đang đỗ chưa (trừ chính xe này)
        if (updated.getTrangThai() == Vehicle.TrangThai.CO_XE &&
            vehicleRepository.existsByKhuAndLoAndTrangThaiAndIdNot(
                updated.getKhu(), updated.getLo(), Vehicle.TrangThai.CO_XE, id)) {
            throw new IllegalArgumentException("Ô " + updated.getKhu() + "-" + updated.getLo() + " đã có xe khác đang đỗ");
        }

        existing.setBienSo(updated.getBienSo());
        existing.setHangXe(updated.getHangXe());
        existing.setKhu(updated.getKhu());
        existing.setLo(updated.getLo());
        existing.setTrangThai(updated.getTrangThai());
        existing.setGhiChu(updated.getGhiChu());

        Vehicle saved = vehicleRepository.save(existing);
        syncService.initiateSync(saved, "UPDATE");
        return saved;
    }

    @Transactional
    public void delete(Long id) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy xe ID: " + id));
        vehicleRepository.deleteById(id);
        syncService.initiateSync(vehicle, "DELETE");
    }

    public List<Vehicle> getByTrangThai(Vehicle.TrangThai trangThai) {
        return vehicleRepository.findByTrangThai(trangThai);
    }
}
