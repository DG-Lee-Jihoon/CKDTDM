package com.parking.repository;

import com.parking.model.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

    Optional<Vehicle> findByBienSo(String bienSo);

    List<Vehicle> findByKhu(String khu);

    List<Vehicle> findByKhuAndLo(String khu, String lo);

    List<Vehicle> findByTrangThai(Vehicle.TrangThai trangThai);

    List<Vehicle> findByHangXe(String hangXe);

    @Query("SELECT DISTINCT v.khu FROM Vehicle v ORDER BY v.khu")
    List<String> findDistinctKhu();

    @Query("SELECT DISTINCT v.lo FROM Vehicle v WHERE v.khu = :khu ORDER BY v.lo")
    List<String> findDistinctLoByKhu(String khu);

    boolean existsByBienSo(String bienSo);

    // Kiểm tra ô (khu+lô) đã có xe đang đỗ chưa (trừ chính xe đang sửa)
    boolean existsByKhuAndLoAndTrangThaiAndIdNot(String khu, String lo, Vehicle.TrangThai trangThai, Long id);

    boolean existsByKhuAndLoAndTrangThai(String khu, String lo, Vehicle.TrangThai trangThai);
}
