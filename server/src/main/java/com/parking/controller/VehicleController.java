package com.parking.controller;

import com.parking.model.Vehicle;
import com.parking.service.VehicleService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/vehicles")
public class VehicleController {

    private final VehicleService vehicleService;

    @Value("${server.id}")
    private int serverId;

    public VehicleController(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    // GET /api/vehicles — lấy toàn bộ
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAll() {
        return ResponseEntity.ok(Map.of(
                "serverId", serverId,
                "data", vehicleService.getAll()
        ));
    }

    // GET /api/vehicles/{id}
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        return vehicleService.getById(id)
                .map(v -> ResponseEntity.ok((Object) v))
                .orElse(ResponseEntity.notFound().build());
    }

    // GET /api/vehicles/search?bienSo=xxx
    @GetMapping("/search")
    public ResponseEntity<?> search(@RequestParam(required = false) String bienSo,
                                    @RequestParam(required = false) String khu) {
        if (bienSo != null) {
            return vehicleService.getByBienSo(bienSo)
                    .map(v -> ResponseEntity.ok((Object) v))
                    .orElse(ResponseEntity.notFound().build());
        }
        if (khu != null) {
            return ResponseEntity.ok(vehicleService.getByKhu(khu));
        }
        return ResponseEntity.ok(vehicleService.getAll());
    }

    // GET /api/vehicles/khu — danh sách khu
    @GetMapping("/khu")
    public ResponseEntity<List<String>> getAllKhu() {
        return ResponseEntity.ok(vehicleService.getAllKhu());
    }

    // POST /api/vehicles — tạo mới
    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody Vehicle vehicle) {
        try {
            Vehicle created = vehicleService.create(vehicle);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", created,
                    "message", "Thêm xe thành công, đang đồng bộ..."
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }

    // PUT /api/vehicles/{id} — cập nhật
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id,
                                    @Valid @RequestBody Vehicle vehicle) {
        try {
            Vehicle updated = vehicleService.update(id, vehicle);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", updated,
                    "message", "Cập nhật thành công, đang đồng bộ..."
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }

    // DELETE /api/vehicles/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            vehicleService.delete(id);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Xóa thành công, đang đồng bộ..."
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }

    // GET /api/vehicles/status — health check + server info
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> status() {
        return ResponseEntity.ok(Map.of(
                "serverId", serverId,
                "status", "UP",
                "totalVehicles", vehicleService.getAll().size()
        ));
    }
}
