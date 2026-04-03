package Client1;

import java.net.*;
import java.io.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Date;
import java.util.Properties;

public class ActPark extends JPanel {

    private JLabel lb_pos, lb_stg, lb_esp, lb_num, lb_clr, lb_time, lb_info, lb_type, lb_sv;
    private JTextField txt_esp, txt_num, txt_type, txt_time;
    private JButton bt_in, bt_out, bt_reset;
    private Choice opt_stg, opt_clr, opt_sv;
    private JScrollPane jsp_info;
    private JTextArea txa_info;
    Socket client;
    DataOutputStream out;
    BufferedReader in;
    String act;
    String lamportS = "0";

    // Đọc IP server từ config.properties
    private Properties loadConfig() {
        Properties p = new Properties();
        try {
            InputStream is = getClass().getResourceAsStream("/config.properties");
            if (is == null) is = new FileInputStream("config.properties");
            p.load(is);
        } catch (Exception e) { }
        return p;
    }

    private String getServerHost(int num) {
        return loadConfig().getProperty("server" + num + ".host", "127.0.0.1");
    }

    public ActPark() {
        setLayout(null);

        lb_sv = new JLabel("Server:");
        lb_sv.setBounds(10, 20, 50, 25);

        lb_pos = new JLabel("Vi tri:");
        lb_pos.setBounds(10, 50, 50, 25);

        lb_stg = new JLabel("Khu");
        lb_stg.setBounds(50, 50, 50, 25);

        lb_esp = new JLabel("lo so");
        lb_esp.setBounds(210, 50, 50, 25);

        lb_num = new JLabel("Bien so");
        lb_num.setBounds(10, 80, 50, 25);

        lb_type = new JLabel("Hang xe");
        lb_type.setBounds(10, 110, 50, 25);

        lb_clr = new JLabel("Mau xe");
        lb_clr.setBounds(210, 110, 50, 25);

        lb_time = new JLabel("Thoi gian");
        lb_time.setBounds(10, 170, 70, 25);

        lb_info = new JLabel("Thong tin");
        lb_info.setBounds(165, 200, 70, 25);

        opt_stg = new Choice();
        opt_stg.setBounds(110, 50, 90, 30);
        for (String k : new String[]{"A","B","C","D","E"}) opt_stg.addItem(k);

        opt_sv = new Choice();
        opt_sv.setBounds(120, 20, 120, 25);
        opt_sv.addItem(" ");
        for (int i = 1; i <= 4; i++) opt_sv.addItem("Server " + i);

        txt_esp = new JTextField();
        txt_esp.setBounds(250, 50, 120, 25);

        txt_num = new JTextField();
        txt_num.setBounds(120, 80, 160, 25);

        txt_type = new JTextField();
        txt_type.setBounds(10, 140, 170, 25);

        txt_time = new JTextField();
        txt_time.setBounds(120, 170, 160, 25);
        new Thread(() -> {
            while (true) {
                try {
                    txt_time.setText(new Date().toLocaleString());
                    Thread.sleep(1000);
                } catch (InterruptedException e) { }
            }
        }).start();

        opt_clr = new Choice();
        opt_clr.setBounds(210, 140, 170, 25);
        for (String c : new String[]{"Khac","Bac","Den","Do","Ghi","Trang","Xanh duong","Xanh luc","Vang"})
            opt_clr.addItem(c);

        txa_info = new JTextArea();
        jsp_info = new JScrollPane(txa_info);
        jsp_info.setBounds(10, 230, 370, 165);

        bt_in = new JButton("Goi xe");
        bt_in.setBounds(75, 400, 70, 25);
        bt_in.addActionListener(e -> { act = "SET"; new Thread(this::runClient).start(); });

        bt_out = new JButton("Tra xe");
        bt_out.setBounds(165, 400, 70, 25);
        bt_out.addActionListener(e -> { act = "DEL"; new Thread(this::runClient).start(); });

        bt_reset = new JButton("Xoa");
        bt_reset.setBounds(255, 400, 70, 25);
        bt_reset.addActionListener(e -> {
            txt_esp.setText(""); txt_num.setText(""); txt_type.setText("");
            txa_info.setText(""); opt_clr.select(0); opt_stg.select(0); opt_sv.select(0);
        });

        add(lb_sv); add(opt_sv); add(lb_pos); add(lb_stg); add(lb_esp);
        add(lb_type); add(lb_num); add(lb_clr); add(lb_time); add(lb_info);
        add(opt_stg); add(txt_esp); add(txt_type); add(txt_num); add(txt_time);
        add(opt_clr); add(jsp_info); add(bt_in); add(bt_out); add(bt_reset);
    }

    private String getMessage() {
        String khu = opt_stg.getSelectedItem();
        String lo  = txt_esp.getText();
        String bs  = txt_num.getText();
        String hieu = txt_type.getText();
        String mau = opt_clr.getSelectedItem();
        String gio = txt_time.getText();
        return khu + lo + "|" + bs + "|" + hieu + "|" + mau + "|" + gio + "|" + act;
    }

    public void runClient() {
        String sv = opt_sv.getSelectedItem().trim();
        if (sv.isEmpty()) {
            txa_info.append("Lỗi: Chưa chọn Server!\n"); return;
        }
        int num;
        try { num = Integer.parseInt(sv.replace("Server ", "")); }
        catch (Exception e) { txa_info.append("Lỗi: Server không hợp lệ!\n"); return; }

        String host = getServerHost(num);
        int port = 2000 + num;
        connect2Server(host, port);
        shutdown();
    }

    public void connect2Server(String destination, int port) {
        try {
            client = new Socket(destination, port);
            in  = new BufferedReader(new InputStreamReader(client.getInputStream()));
            out = new DataOutputStream(client.getOutputStream());
            txa_info.append("Đã kết nối đến " + destination + ":" + port + "\n");
            String message = getMessage();
            out.writeBytes("@$0|0000|" + lamportS + "|Client|Send|1|123$" + message + "$@");
            out.write(13); out.write(10); out.flush();
            String inLine = in.readLine();
            txa_info.append("Thông báo: " + inLine + "\n\n");
        } catch (Exception e) {
            txa_info.append("Lỗi: Không thể kết nối đến Server! (" + e.getMessage() + ")\n");
        }
    }

    public void shutdown() {
        try { if (client != null) client.close(); }
        catch (IOException ex) { txa_info.append("Lỗi IO đóng kết nối!\n"); }
    }
}
