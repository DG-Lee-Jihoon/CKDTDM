#!/bin/bash
# =====================================================
# start-all.sh — Chạy 4 Spring Boot server song song
# Mỗi server dùng port, DB và peer list riêng
# =====================================================

JAR="target/parking-sync-server-1.0.0.jar"
DB_USER="root"
DB_PASS="root"
LOG_DIR="logs"

mkdir -p "$LOG_DIR"

# Build project trước (bỏ qua nếu đã build)
if [ ! -f "server/$JAR" ]; then
  echo "▶ Building project..."
  cd server && mvn clean package -DskipTests -q && cd ..
fi

echo "▶ Khởi động 4 server..."

# SERVER 1 — port 8081, DB port 3307
java -jar server/$JAR \
  -DSERVER_ID=1 \
  -DSERVER_PORT=8081 \
  -DDB_PORT=3307 \
  -DDB_USER=$DB_USER \
  -DDB_PASS=$DB_PASS \
  -DSYNC_PEERS="http://localhost:8082,http://localhost:8083,http://localhost:8084" \
  > $LOG_DIR/server1.log 2>&1 &
echo "  ✓ Server 1 → http://localhost:8081 (PID: $!)"

# SERVER 2 — port 8082, DB port 3308
java -jar server/$JAR \
  -DSERVER_ID=2 \
  -DSERVER_PORT=8082 \
  -DDB_PORT=3308 \
  -DDB_USER=$DB_USER \
  -DDB_PASS=$DB_PASS \
  -DSYNC_PEERS="http://localhost:8081,http://localhost:8083,http://localhost:8084" \
  > $LOG_DIR/server2.log 2>&1 &
echo "  ✓ Server 2 → http://localhost:8082 (PID: $!)"

# SERVER 3 — port 8083, DB port 3309
java -jar server/$JAR \
  -DSERVER_ID=3 \
  -DSERVER_PORT=8083 \
  -DDB_PORT=3309 \
  -DDB_USER=$DB_USER \
  -DDB_PASS=$DB_PASS \
  -DSYNC_PEERS="http://localhost:8081,http://localhost:8082,http://localhost:8084" \
  > $LOG_DIR/server3.log 2>&1 &
echo "  ✓ Server 3 → http://localhost:8083 (PID: $!)"

# SERVER 4 — port 8084, DB port 3310
java -jar server/$JAR \
  -DSERVER_ID=4 \
  -DSERVER_PORT=8084 \
  -DDB_PORT=3310 \
  -DDB_USER=$DB_USER \
  -DDB_PASS=$DB_PASS \
  -DSYNC_PEERS="http://localhost:8081,http://localhost:8082,http://localhost:8083" \
  > $LOG_DIR/server4.log 2>&1 &
echo "  ✓ Server 4 → http://localhost:8084 (PID: $!)"

echo ""
echo "✅ Tất cả server đã khởi động!"
echo "🌐 Mở client: client/index.html"
echo ""
echo "📋 Logs:"
echo "   tail -f logs/server1.log"
echo "   tail -f logs/server2.log"
