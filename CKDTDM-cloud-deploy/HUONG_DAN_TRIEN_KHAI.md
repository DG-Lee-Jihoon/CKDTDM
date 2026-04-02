# HƯỚNG DẪN TRIỂN KHAI HỆ THỐNG TRÊN 4 MÁY THẬT

## Kiến trúc hệ thống

```
Client (WinForms - máy người dùng)
   ↓ TCP Socket
   ├─→ Server1 (Máy TV1) → MySQL1 (DB riêng)
   ├─→ Server2 (Máy TV2) → MySQL2 (DB riêng)  
   ├─→ Server3 (Máy TV3) → MySQL3 (DB riêng)
   ├─→ Server4 (Máy TV4) → MySQL4 (DB riêng)
   └─→ Server5 (Máy TV5) → MySQL5 (DB riêng)

Vòng tròn ảo (4 pha đồng bộ):
S1 ⟷ S2 ⟷ S3 ⟷ S4 ⟷ S5 (vòng kín)
```

## Cơ chế 4 pha đồng bộ dữ liệu

Khi Client gửi request đến bất kỳ Server nào:

1. **Locked** (Khóa): Server nhận request → khóa trường dữ liệu → chuyển tiếp vòng tròn
2. **Temped** (Tạm): Tạo bảng tạm → quay vòng ngược
3. **Updated** (Cập nhật): Cập nhật CSDL chính → quay vòng ngược  
4. **Synchronymed** (Đồng bộ): Kiểm tra đồng bộ hóa → kết thúc vòng tròn ảo

**Lưu ý:** Tất cả 5 DB luôn có dữ liệu giống nhau (cùng 1 bản ghi xé)

---

## BƯỚC 1: Chuẩn bị 4-5 máy tính

Giả sử bạn có 4 máy:
- **Máy 1 (TV1):** IP = `192.168.1.101` → chạy Server1
- **Máy 2 (TV2):** IP = `192.168.1.102` → chạy Server2  
- **Máy 3 (TV3):** IP = `192.168.1.103` → chạy Server3
- **Máy 4 (TV4):** IP = `192.168.1.104` → chạy Server4 + Server5

Hoặc 5 máy riêng biệt cho từng server.

---

## BƯỚC 2: Cài đặt MySQL trên từng máy

Mỗi máy cần cài MySQL local:

```sql
CREATE DATABASE dtdm;
USE dtdm;

CREATE TABLE server1 (
  vitri  VARCHAR(10) PRIMARY KEY,
  bienso VARCHAR(20),
  hieu   VARCHAR(20),
  mau    VARCHAR(20),
  gio    VARCHAR(50)
);

-- Tương tự tạo server2, server3, server4, server5
CREATE TABLE server2 LIKE server1;
CREATE TABLE server3 LIKE server1;
CREATE TABLE server4 LIKE server1;
CREATE TABLE server5 LIKE server1;
```

**Lưu ý:** Mỗi máy chỉ cần tạo bảng tương ứng với Server chạy trên máy đó.

---

## BƯỚC 3: Cấu hình IP trong config.properties

Chỉnh file `src/config.properties` theo IP thực tế:

```properties
# Thay đổi IP theo máy thật của bạn
server1.host=192.168.1.101
server2.host=192.168.1.102
server3.host=192.168.1.103
server4.host=192.168.1.104
server5.host=192.168.1.105

server1.port=2001
server2.port=2002
server3.port=2003
server4.port=2004
server5.port=2005

# MySQL local (mỗi máy kết nối localhost)
db.url=jdbc:mysql://localhost:3306/dtdm
db.user=root
db.password=
```

---

## BƯỚC 4: Build project

```bash
cd CKDTDM-cloud-deploy
ant clean
ant jar
```

Kết quả: file `build/CKDTDM-cloud-deploy.jar`

---

## BƯỚC 5: Copy file JAR + config lên từng máy

Trên mỗi máy, tạo thư mục:

```
/opt/parking-system/
  ├── CKDTDM-cloud-deploy.jar
  ├── config.properties  (chỉnh IP đúng)
  └── lib/
      └── mysql-connector-j-9.6.0.jar
```

**Quan trọng:** File `config.properties` phải giống nhau trên tất cả các máy (cùng IP).

---

## BƯỚC 6: Chạy Server trên từng máy

### Máy 1 (TV1):
```bash
java -cp "CKDTDM-cloud-deploy.jar:lib/*" Server1.Server1
```

### Máy 2 (TV2):
```bash
java -cp "CKDTDM-cloud-deploy.jar:lib/*" Server2.Server2
```

### Máy 3 (TV3):
```bash
java -cp "CKDTDM-cloud-deploy.jar:lib/*" Server3.Server3
```

### Máy 4 (TV4):
```bash
# Chạy 2 server cùng lúc (2 terminal)
java -cp "CKDTDM-cloud-deploy.jar:lib/*" Server4.Server4
java -cp "CKDTDM-cloud-deploy.jar:lib/*" Server5.Server5
```

**Windows:** Thay `:` bằng `;` trong classpath

---

## BƯỚC 7: Chạy Client (máy người dùng)

```bash
java -cp "CKDTDM-cloud-deploy.jar:lib/*" Client1.Client
```

Giao diện WinForms sẽ hiện ra:
- Chọn Server (1-5)
- Nhập thông tin xe
- Nhấn "Gọi xe" hoặc "Trả xe"

---

## Kiểm tra hoạt động

1. Mở Client → chọn Server1 → Gọi xe (vị trí A1)
2. Quan sát log trên 5 máy server:
   - Server1: nhận request → Locked → chuyển S2
   - Server2: nhận Locked → chuyển S3
   - Server3: nhận Locked → chuyển S4
   - Server4: nhận Locked → chuyển S5
   - Server5: nhận Locked (start=4) → Temped → quay ngược S4
   - ... (4 pha hoàn tất)
3. Kiểm tra MySQL trên cả 5 máy → dữ liệu giống nhau

---

## Xử lý lỗi

- **Không kết nối được Server:** Kiểm tra firewall, mở port 2001-2005
- **DB lỗi:** Kiểm tra MySQL đang chạy, user/password đúng
- **Vòng tròn bị đứt:** Kiểm tra tất cả 5 server đang chạy

---

## Lưu ý quan trọng

✅ Tất cả máy phải cùng mạng LAN hoặc có kết nối mạng với nhau  
✅ File `config.properties` phải giống nhau trên tất cả máy  
✅ Mỗi máy chạy MySQL local riêng (không dùng chung DB)  
✅ Cơ chế 4 pha đảm bảo 5 DB luôn đồng bộ  
✅ Nếu 1 server chết, hệ thống tự động bỏ qua (xem logic `catch Exception` trong code)
