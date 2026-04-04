
import socket

def start_server(host='0.0.0.0', port=12345):
    server_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    server_socket.bind((host, port))
    server_socket.listen(1)
    print(f"✅ Server đang lắng nghe tại {host}:{port}")

    conn, addr = server_socket.accept()
    print(f"🔗 Kết nối từ {addr}")

    while True:
        data = conn.recv(1024).decode('utf-8')
        if not data:
            break

        print("📩 Nhận từ client:", data)
        if data.lower() == 'exit':
            conn.send("Kết nối đóng.".encode('utf-8'))
            break

        # Đảo chuỗi + in hoa
        reversed_str = data[::-1].upper()
        conn.send(reversed_str.encode('utf-8'))

    conn.close()
    print("❌ Kết nối đóng.")

if __name__ == "__main__":
    start_server()
