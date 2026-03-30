/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Client1;

import java.net.*;
import java.io.*;
import javax.swing.*;
import java.awt.*;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.*;
import java.awt.event.WindowAdapter;
import javax.swing.event.*;
import javax.swing.border.TitledBorder;
import java.util.Date;

public class ActPark extends JPanel {

    private JLabel lb_pos, lb_stg, lb_esp, lb_num, lb_clr, lb_time, lb_info, lb_type, lb_sv;
    private JTextField txt_stg, txt_esp, txt_num, txt_clr, txt_time, txt_info, txt_type;
    private JButton bt_in, bt_out, bt_reset;
    private Choice opt_stg, opt_clr, opt_sv;
    private JScrollPane jsp_info;
    private JTextArea txa_info;
    Socket client;
    DataOutputStream out;
    BufferedReader in;
    String message = "", act, position = "", lamportS = "";
    String er = "";

    public ActPark() {

        setLayout(null);

        lb_sv = new JLabel();
        lb_sv.setBounds(new Rectangle(10, 20, 50, 25));
        lb_sv.setText("Server:");

        lb_pos = new JLabel();
        lb_pos.setBounds(new Rectangle(10, 50, 50, 25));
        lb_pos.setText("Vi tri:");

        lb_stg = new JLabel();
        lb_stg.setBounds(new Rectangle(50, 50, 50, 25));
        lb_stg.setText("Khu");

        lb_esp = new JLabel();
        lb_esp.setBounds(new Rectangle(210, 50, 50, 25));
        lb_esp.setText("lo so");

        lb_num = new JLabel();
        lb_num.setBounds(new Rectangle(10, 80, 50, 25));
        lb_num.setText("Bien so");

        lb_type = new JLabel();
        lb_type.setBounds(new Rectangle(10, 110, 50, 25));
        lb_type.setText("Hang xe");

        lb_clr = new JLabel();
        lb_clr.setBounds(new Rectangle(210, 110, 50, 25));
        lb_clr.setText("Mau xe");

        lb_time = new JLabel();
        lb_time.setBounds(new Rectangle(10, 170, 70, 25));
        lb_time.setText("Thoi gian");

        lb_info = new JLabel();
        lb_info.setBounds(new Rectangle(165, 200, 70, 25));
        lb_info.setText("Thong tin");

        opt_stg = new Choice();
        opt_stg.setBounds(new Rectangle(110, 50, 90, 30));
        opt_stg.addItem("A");
        opt_stg.addItem("B");
        opt_stg.addItem("C");
        opt_stg.addItem("D");
        opt_stg.addItem("E");

        opt_sv = new Choice();
        opt_sv.setBounds(new Rectangle(120, 20, 120, 25));
        opt_sv.addItem(" ");
        opt_sv.addItem("Server 1");
        opt_sv.addItem("Server 2");
        opt_sv.addItem("Server 3");
        opt_sv.addItem("Server 4");
        opt_sv.addItem("Server 5");
        opt_sv.addItemListener(new ItemListener() {

            public void itemStateChanged(ItemEvent e) {
                if (e.getItem() == " ") {
                } else if (e.getItem() == "Server 1") {
                    UDPMulticastClient("224.0.0.0");
                } else if (e.getItem() == "Server 2") {
                    UDPMulticastClient("235.0.0.1");
                } else if (e.getItem() == "Server 3") {
                    UDPMulticastClient("235.255.0.1");
                } else if (e.getItem() == "Server 4") {
                    UDPMulticastClient("225.4.5.6");
                } else {
                    UDPMulticastClient("224.0.255.1");
                }

            }
        });
        /*
         * txt_sv = new JTextField(); txt_sv.setBounds(new Rectangle(250, 20,
         * 120, 25));
         */

        txt_esp = new JTextField();

        txt_esp.setBounds(
                new Rectangle(250, 50, 120, 25));

        txt_num = new JTextField();

        txt_num.setBounds(
                new Rectangle(120, 80, 160, 25));

        txt_time = new JTextField();

        txt_time.setBounds(
                new Rectangle(120, 170, 160, 25));
        new Thread(
                new Runnable() {

                    public void run() {
                        while (true) {
                            try {
                                txt_time.setText(getTime());
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                            }
                        }
                    }
                }).start();

        opt_clr = new Choice();
        opt_clr.setBounds(new Rectangle(210, 140, 170, 25));
        opt_clr.addItem("Khac");
        opt_clr.addItem("Bac");
        opt_clr.addItem("Den");
        opt_clr.addItem("Do");
        opt_clr.addItem("Ghi");
        opt_clr.addItem("Trang");
        opt_clr.addItem("Xanh duong");
        opt_clr.addItem("Xanh luc");
        opt_clr.addItem("Vang");

        txt_type = new JTextField();
        txt_type.setBounds(new Rectangle(10, 140, 170, 25));

        txa_info = new JTextArea();
        jsp_info = new JScrollPane();
        txa_info.setBounds(new Rectangle(10, 230, 370, 165));
        jsp_info.setViewportView(txa_info);
        jsp_info.setBounds(new Rectangle(10, 230, 370, 165));

        bt_in = new JButton("Goi xe");
        bt_in.setBounds(new Rectangle(75, 400, 70, 25));
        bt_in.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent e) {
                act = "SET";
                new Thread(new Runnable() { public void run() { runClient(); } }).start();
            }
        });

        bt_out = new JButton("Tra xe");
        bt_out.setBounds(new Rectangle(165, 400, 70, 25));
        bt_out.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent e) {
                act = "DEL";
                new Thread(new Runnable() { public void run() { runClient(); } }).start();
            }
        });

        bt_reset = new JButton("Xoa");
        bt_reset.setBounds(new Rectangle(255, 400, 70, 25));
        bt_reset.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent e) {
                txt_esp.setText("");
                txt_num.setText("");
                txt_type.setText("");
                txa_info.setText("");
                opt_clr.select(0);
                opt_stg.select(0);
                opt_sv.select(0);
            }
        });

        add(lb_sv);
        add(opt_sv);
        add(lb_pos);
        add(lb_stg);
        add(lb_esp);
        add(lb_type);
        add(lb_num);
        add(lb_clr);
        add(lb_time);
        add(lb_info);
        add(opt_stg);
        add(txt_esp);
        add(txt_type);
        add(txt_num);
        add(txt_time);
        add(opt_clr);
        add(jsp_info);
        add(bt_in);
        add(bt_out);
        add(bt_reset);

    }

    public String getTime() {
        Date now = new Date();
        String time = now.toLocaleString();
        return time;
    }

    public String getMessage() {
        String khu = opt_stg.getSelectedItem();
        String lo = txt_esp.getText();
        String bs = txt_num.getText();
        String hieu = txt_type.getText();
        String mau = opt_clr.getSelectedItem();
        String gio = txt_time.getText();
        String info = khu + lo + "|" + bs + "|" + hieu + "|" + mau + "|" + gio + "|" + act;
        return (info);
    }

    public void runClient() {
        message = getMessage();
        int ports;
        if (message.equalsIgnoreCase("")) {
            txa_info.append("Loi: Vui long nhap vao thong diep! \n");
            return;
        }
        String sv = opt_sv.getSelectedItem();

        switch (sv) {
            case (" "): {
                er = "Can chon Server";
                ports = 0;

                break;
            }
            case ("Server 1"): {
                ports = 2001;
                break;
            }
            case ("Server 2"): {
                ports = 2002;
                break;
            }
            case ("Server 3"): {
                ports = 2003;
                break;
            }
            case ("Server 4"): {
                ports = 2004;
                break;
            }
            case ("Server 5"): {
                ports = 2005;
                break;
            }
            default: {
                ports = 2001;
                break;
            }
        }
        connect2Server("127.0.0.1", ports);
        shutdown();
    }

    public void connect2Server(String destination, int port) {
        try {
            client = new Socket(destination, port);
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            out = new DataOutputStream(client.getOutputStream());
            txa_info.append("Da ket noi den Server tai cong " + port + ".\n");
            message = getMessage();
            out.writeBytes("@$0|00000|" + lamportS + "|Client|Send|1|123$$" + message + "$@");
            out.write(13);
            out.write(10);
            out.flush();
            String inLine = in.readLine();
            txa_info.append("Thong bao: " + inLine + "\n\n");
        } catch (Exception e) {
            txa_info.append("Loi: Khong the ket noi den Server! Vui long thu lai.\n");
        }
    }

    public void shutdown() {
        try {
            client.close();
        } catch (IOException ex) {
            txa_info.append("Loi IO dong ket noi!!!\n");
        }
    }

    public void UDPMulticastClient(String addressM) {
        //int port = 3457;
        /*
         * UDPMulticastClient("224.0.0.0") UDP server 1: 224.0.0.0 UDP server 2:
         * 235.0.0.1 UDP server 3: 235.255.0.1 UDP Server 4: 225.4.5.6 UDP
         * Server 5: 228.5.6.7
         */
        MulticastSocket socketRece;
        try {
            //String addressM = "235.0.0.1";
            byte[] buffer1 = new byte[65535];
            InetAddress addM = InetAddress.getByName(addressM);
            MulticastSocket socketM = new MulticastSocket();

            String mess = "#send";
            byte message[] = mess.getBytes();
            int portM = 5432;
            DatagramPacket packetM = new DatagramPacket(message, message.length, addM, portM);
            socketM.send(packetM);
            Boolean ktraguive = true;
            while (ktraguive) {
                String address = "224.1.2.3";
                socketRece = new MulticastSocket(portM);
                InetAddress add = InetAddress.getByName(address);
                socketRece.joinGroup(add);

//                display.append("dang cho ket noi lay Lamport\n");

                // Receive request from client
                DatagramPacket packet = new DatagramPacket(buffer1, buffer1.length);
                socketRece.receive(packet);
                String dataReceiveS = new String(packet.getData(), 0, packet.getLength());
                String msNhan = dataReceiveS;
                if (msNhan != null) {
                    //System.out.println(msNhan);
                    lamportS = msNhan;
                    ktraguive = false;
                }
                //break;
            }

//            DatagramPacket packet = new DatagramPacket(buffer1, buffer1.length);
//            socketU.receive(packet);
//            //check = true;
//            //addres = packet.getAddress().toString();
//            InetAddress client = packet.getAddress();
//            String dataReceive = new String(packet.getData(), 0, packet.getLength());
//            String temp = dataReceive;
        } catch (IOException io) {
            txa_info.append("khong ket noi duoc");
        }

    }
}