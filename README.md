# 🚗 Parking Sync — Hệ thống quản lý bãi xe đồng bộ 4 server

## Kiến trúc
```
Browser (HTML/JS)
    │  REST API + WebSocket (STOMP)
    ▼
Server 1 (Spring Boot :8081) ←──── đồng bộ 4 pha ────→ Server 2 (:8082)
    │  JPA/Hibernate                                         │
    ▼                                                        ▼
MySQL DB 1                                            MySQL DB 2

Server 3 (Spring Boot :8083) ←────────────────────→ Server 4 (:8084)
    │                                                        │
    ▼                                                        ▼
MySQL DB 3                                            MySQL DB 4
```

## Cơ chế 4 pha đồng bộ
```
Client ghi → Server X
    ↓
[PHA 1] LOCKED   — Server X khóa bản ghi, thông báo 3 peer còn lại chuẩn bị
    ↓
[PHA 2] TEMPED   — Ghi tạm vào bộ nhớ, broadcast tới peers để lưu temp
    ↓
[PHA 3] UPDATED  — Ghi chính thức vào MySQL, peers cũng ghi vào DB riêng
    ↓
[PHA 4] SYNCED   — Xác nhận hoàn tất, thông báo tới tất cả client qua WebSocket
```

## Cài đặt

### Yêu cầu
- Java 17+
- Maven 3.8+
- MySQL 8.0+

### 1. Tạo database
```bash
mysql -u root -p < setup-db.sql
```

### 2. Build project
```bash
cd server
mvn clean package -DskipTests
cd ..
```

### 3. Chạy 4 server
```bash
chmod +x start-all.sh
./start-all.sh
```

Hoặc chạy từng server thủ công:
```bash
# Server 1
java -jar server/target/parking-sync-server-1.0.0.jar \
  -DSERVER_ID=1 -DSERVER_PORT=8081 -DDB_PORT=3307 \
  -DSYNC_PEERS="http://localhost:8082,http://localhost:8083,http://localhost:8084"

# Server 2
java -jar server/target/parking-sync-server-1.0.0.jar \
  -DSERVER_ID=2 -DSERVER_PORT=8082 -DDB_PORT=3308 \
  -DSYNC_PEERS="http://localhost:8081,http://localhost:8083,http://localhost:8084"

# Server 3
java -jar server/target/parking-sync-server-1.0.0.jar \
  -DSERVER_ID=3 -DSERVER_PORT=8083 -DDB_PORT=3309 \
  -DSYNC_PEERS="http://localhost:8081,http://localhost:8082,http://localhost:8084"

# Server 4
java -jar server/target/parking-sync-server-1.0.0.jar \
  -DSERVER_ID=4 -DSERVER_PORT=8084 -DDB_PORT=3310 \
  -DSYNC_PEERS="http://localhost:8081,http://localhost:8082,http://localhost:8083"
```

### 4. Mở client
Mở file `client/index.html` bằng trình duyệt (Chrome/Firefox).

> **Lưu ý**: Nếu gặp lỗi CORS, chạy client qua Live Server hoặc:
> ```bash
> npx serve client
> # Mở http://localhost:3000
> ```

## REST API

| Method | URL | Mô tả |
|--------|-----|-------|
| GET    | /api/vehicles | Lấy tất cả xe |
| GET    | /api/vehicles/{id} | Lấy xe theo ID |
| GET    | /api/vehicles/search?bienSo=xxx | Tìm theo biển số |
| POST   | /api/vehicles | Thêm xe mới |
| PUT    | /api/vehicles/{id} | Cập nhật xe |
| DELETE | /api/vehicles/{id} | Xóa xe |
| GET    | /api/vehicles/status | Health check |

## WebSocket

Connect tới `ws://localhost:808X/ws` (SockJS + STOMP)

Subscribe:
- `/topic/sync` — nhận sync events 4 pha
- `/topic/vehicles` — nhận cập nhật xe

## Cấu trúc dữ liệu xe
```json
{
  "bienSo": "51A-12345",
  "hangXe": "Toyota Camry",
  "khu": "A",
  "lo": "01",
  "trangThai": "CO_XE",
  "ghiChu": "Xe VIP"
}
```

Trạng thái: `CHO_XE` | `CO_XE` | `BAO_TRI` | `DAT_TRUOC`

## Cấu trúc project
```
parking-sync/
├── server/
│   ├── pom.xml
│   └── src/main/java/com/parking/
│       ├── ParkingSyncApplication.java
│       ├── config/
│       │   ├── AppConfig.java          # CORS + RestTemplate
│       │   └── WebSocketConfig.java    # STOMP WebSocket
│       ├── controller/
│       │   ├── VehicleController.java  # REST API
│       │   └── SyncController.java     # Nhận sync từ peer
│       ├── model/
│       │   ├── Vehicle.java            # Entity
│       │   └── SyncEvent.java          # Gói tin 4 pha
│       ├── repository/
│       │   └── VehicleRepository.java
│       ├── service/
│       │   └── VehicleService.java
│       └── sync/
│           └── SyncService.java        # Logic 4 pha
├── client/
│   └── index.html                      # Frontend HTML/JS
├── setup-db.sql
├── start-all.sh
└── README.md
```
