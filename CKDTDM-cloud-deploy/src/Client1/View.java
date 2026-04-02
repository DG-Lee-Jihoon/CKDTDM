package Client1;

import java.io.*;
import java.net.*;
import java.awt.*;
import javax.swing.*;
import java.util.Properties;

public class View extends JPanel {

    private JTextArea display;
    private JScrollPane pane;
    private JButton bt_view;
    Socket client;
    DataOutputStream out;
    BufferedReader in;

    private String getServerHost(int num) {
        Properties p = new Properties();
        try {
            InputStream is = getClass().getResourceAsStream("/config.properties");
            if (is == null) is = new FileInputStream("config.properties");
            p.load(is);
        } catch (Exception e) { }
        return p.getProperty("server1.host", "127.0.0.1");
    }

    public View() {
        setLayout(null);

        display = new JTextArea();
        pane = new JScrollPane(display);
        pane.setBounds(5, 5, 380, 360);

        bt_view = new JButton("Xem");
        bt_view.setBounds(165, 370, 70, 25);
        bt_view.addActionListener(e -> {
            display.setText("");
            new Thread(this::runClient).start();
        });

        add(pane);
        add(bt_view);
    }

    public void runClient() {
        // Xem dữ liệu từ Server1 (có thể mở rộng cho phép chọn server)
        String host = getServerHost(1);
        connect2Server(host, 2001);
        shutdown();
    }

    public void connect2Server(String destination, int port) {
        try {
            String message = "|||||VIEW";
            client = new Socket(destination, port);
            in  = new BufferedReader(new InputStreamReader(client.getInputStream()));
            out = new DataOutputStream(client.getOutputStream());
            out.writeBytes("@$0|00000|0|Client|Send|1|123$" + message + "$@");
            out.write(13); out.write(10); out.flush();

            String inLine = in.readLine();
            display.append("Vi tri | Bien so | Hieu xe | Mau xe | Gio den\n");
            display.append("-----------------------------------------------\n");
            while (inLine != null && !inLine.isEmpty()) {
                String[] parts = inLine.split("\\|");
                if (parts.length >= 5) {
                    display.append(String.join(" | ", parts[0], parts[1], parts[2], parts[3], parts[4]) + "\n");
                    // bỏ 5 field đã đọc
                    int skip = 0;
                    for (int i = 0; i < 5; i++) {
                        int idx = inLine.indexOf("|");
                        if (idx < 0) break;
                        inLine = inLine.substring(idx + 1);
                    }
                } else break;
            }
        } catch (Exception e) {
            display.append("Lỗi kết nối: " + e.getMessage() + "\n");
        }
    }

    public void shutdown() {
        try { if (client != null) client.close(); }
        catch (IOException ex) { }
    }
}
