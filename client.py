import socket
import threading
import tkinter as tk
from tkinter import messagebox

class ClientGUI:
    def __init__(self, master):
        self.master = master
        master.title("TCP Client - Đảo ngược chuỗi")
        master.geometry("400x350")

        # IP và Port
        tk.Label(master, text="Server IP:").pack()
        self.entry_ip = tk.Entry(master)
        self.entry_ip.insert(0, "10.1.252.208")
        self.entry_ip.pack()

        tk.Label(master, text="Port:").pack()
        self.entry_port = tk.Entry(master)
        self.entry_port.insert(0, "12345")
        self.entry_port.pack()

        self.btn_connect = tk.Button(master, text="Kết nối", command=self.connect_to_server)
        self.btn_connect.pack(pady=5)

        # Khung hiển thị
        self.text_area = tk.Text(master, height=10, width=45)
        self.text_area.pack(pady=5)
        self.text_area.config(state=tk.DISABLED)

        # Nhập chuỗi
        self.entry_msg = tk.Entry(master, width=30)
        self.entry_msg.pack(side=tk.LEFT, padx=5)
        self.btn_send = tk.Button(master, text="Gửi", command=self.send_message)
        self.btn_send.pack(side=tk.LEFT)

        self.socket = None

    def connect_to_server(self):
        try:
            ip = self.entry_ip.get()
            port = int(self.entry_port.get())
            self.socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
            self.socket.connect((ip, port))
            self.append_text(f"✅ Đã kết nối tới {ip}:{port}")

            # Thread nhận dữ liệu từ server
            threading.Thread(target=self.receive_messages, daemon=True).start()
        except Exception as e:
            messagebox.showerror("Lỗi", f"Không thể kết nối server: {e}")

    def receive_messages(self):
        while True:
            try:
                data = self.socket.recv(1024).decode('utf-8')
                if not data:
                    break
                self.append_text(f"Server: {data}")
                if data == "Kết nối đóng.":
                    self.socket.close()
                    break
            except:
                break

    def send_message(self):
        if not self.socket:
            messagebox.showwarning("Chưa kết nối", "Vui lòng kết nối tới server trước!")
            return

        msg = self.entry_msg.get()
        if msg:
            self.socket.send(msg.encode('utf-8'))
            self.append_text(f"Client: {msg}")
            self.entry_msg.delete(0, tk.END)
            if msg.lower() == "exit":
                self.socket.close()

    def append_text(self, text):
        self.text_area.config(state=tk.NORMAL)
        self.text_area.insert(tk.END, text + "\n")
        self.text_area.config(state=tk.DISABLED)
        self.text_area.see(tk.END)


if __name__ == "__main__":
    root = tk.Tk()
    app = ClientGUI(root)
    root.mainloop()
