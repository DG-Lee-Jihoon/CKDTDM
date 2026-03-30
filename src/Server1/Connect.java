/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Server1;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

// lop tao doi tuong ket noi den mot Server khac neu Server co yeu cau
public class Connect extends Thread {

    String destination;
    int port;
    String serverName;
    Socket connection;
    DataOutputStream out;
    BufferedReader in;

    public Connect(String destination, int port, String serverName) {
        this.destination = destination;
        this.port = port;
        this.serverName = serverName;
    }

    public void connect() {
        try {
            connection = new Socket(destination, port);
            in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            out = new DataOutputStream(connection.getOutputStream());
            System.out.println("Connected to " + serverName + " at port " + port + ".");
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void requestServer(String message) {
        try {
            System.out.println("Send a message to: " + serverName);
            System.out.println("Message: " + message.substring(message.lastIndexOf("%%") + 2));
            System.out.flush();
            out.writeBytes(message);
            out.write(13);
            out.write(10);
            out.flush();
            System.out.println();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void shutdown() {
        try {
            connection.close();
        } catch (IOException ex) {
            System.out.println("IO error closing socket");
        }
    }

    public void run(String s) {
        requestServer(s);
        shutdown();
    }
}
