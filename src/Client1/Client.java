/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Client1;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Client extends JFrame {

    private JFrame mainFrm;
    private JTabbedPane tab;

    public Client() {

        mainFrm = new JFrame("He thong Bai do xe o to");
        mainFrm.setSize(400, 500);

        tab = new JTabbedPane();
        tab.addTab("Goi-Tra xe", new ActPark());
        tab.addTab("Hien trang", new View());

        mainFrm.add(tab);
        mainFrm.setVisible(true);
        mainFrm.setResizable(false);

    }

    public static void main(String args[]) {
        Client app = new Client();
        app.addWindowListener(new WindowAdapter() {

            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
    }
}