/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Client1;

import java.io.*;
import java.net.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import java.awt.Dimension;
import java.awt.GridLayout;

public class View extends JPanel {

    private boolean DEBUG = false;
    private JTable table;
    private JButton bt_view;
    private JTextArea display;
    private JScrollPane pane;
    Socket client, connection;
    ServerSocket server;
    DataOutputStream out;
    BufferedReader in;

    public View() {
        setLayout(null);

        display = new JTextArea();
        display.setBounds(new Rectangle(5, 5, 380, 360));
        pane = new JScrollPane();
        pane.setViewportView(display);
        pane.setBounds(new Rectangle(5, 5, 380, 360));

        bt_view = new JButton("Xem");
        bt_view.setBounds(new Rectangle(165, 370, 70, 25));
        bt_view.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent e) {
                display.setText("");
                new Thread(new Runnable() { public void run() { runClient(); } }).start();
            }
        });
        add(pane);
        add(bt_view);

    }

    public void runClient() {
        connect2Server("127.0.0.1", 2001);
        shutdown();
    }

    public void connect2Server(String destination, int port) {
        try {
            String message = "|||||VIEW";
            client = new Socket(destination, port);
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            out = new DataOutputStream(client.getOutputStream());
            out.writeBytes("@$0|000|145|Client|Send|1|123$$" + message + "$@");
            out.write(13);
            out.write(10);
            out.flush();
            String inLine = in.readLine();
            int k = 0, j = 0;
            display.append("Vi tri |  Bien so  |  Hieu xe  |  Mau xe  |    Gio den \n");
            while (!inLine.equalsIgnoreCase("")) {
                for (int i = 0; i < 5; i++) {
                    try {
                        k = inLine.indexOf("|");
                    } catch (Exception ex) {
                        k = 0;
                    }
                    String vt = inLine.substring(0, k);
                    display.append(vt + "      |");
                    k += 1;
                    inLine = inLine.substring(k);
                }
                display.append("\n");
                j++;
            }
            table.setValueAt(inLine, 0, 1);
            System.out.print(inLine);
            //table.setValueAt(inLine,1,1);

        } catch (Exception e) {
        }
    }

    public void shutdown() {
        try {
            client.close();
        } catch (IOException ex) {
        }
    }
}
