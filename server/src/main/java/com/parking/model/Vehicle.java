package com.parking.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Entity
@Table(name = "vehicles")
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Biển số không được trống")
    @Column(name = "bien_so", unique = true, nullable = false, length = 20)
    private String bienSo;

    @NotBlank(message = "Hãng xe không được trống")
    @Column(name = "hang_xe", nullable = false, length = 50)
    private String hangXe;

    @NotBlank(message = "Khu không được trống")
    @Column(name = "khu", nullable = false, length = 10)
    private String khu;

    @NotBlank(message = "Lô không được trống")
    @Column(name = "lo", nullable = false, length = 10)
    private String lo;

    @Enumerated(EnumType.STRING)
    @Column(name = "trang_thai", nullable = false)
    private TrangThai trangThai = TrangThai.CHO_XE;

    @Column(name = "ghi_chu", length = 255)
    private String ghiChu;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "updated_by_server")
    private Integer updatedByServer;

    public enum TrangThai {
        CHO_XE, CO_XE, BAO_TRI, DAT_TRUOC
    }

    public Vehicle() {}

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getBienSo() { return bienSo; }
    public void setBienSo(String bienSo) { this.bienSo = bienSo; }

    public String getHangXe() { return hangXe; }
    public void setHangXe(String hangXe) { this.hangXe = hangXe; }

    public String getKhu() { return khu; }
    public void setKhu(String khu) { this.khu = khu; }

    public String getLo() { return lo; }
    public void setLo(String lo) { this.lo = lo; }

    public TrangThai getTrangThai() { return trangThai; }
    public void setTrangThai(TrangThai trangThai) { this.trangThai = trangThai; }

    public String getGhiChu() { return ghiChu; }
    public void setGhiChu(String ghiChu) { this.ghiChu = ghiChu; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public Integer getUpdatedByServer() { return updatedByServer; }
    public void setUpdatedByServer(Integer updatedByServer) { this.updatedByServer = updatedByServer; }
}
