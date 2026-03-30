/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Server2;

import java.io.*;
import java.net.*;
import java.util.Hashtable;

public class Server2 {

    static java.io.PrintStream display = System.out;
    int counter;
    ObjectOutputStream output;
    ObjectInputStream input;
    ServerSocket server;
    Socket client, connection;
    String serverName;
    String type;
    int pos;
    RountingTable rount;
    int currentCircle;
    static String MESSAGE, replyMessage;
    Hashtable hash;
    DataOutputStream out;
    BufferedReader in;
    Database db1, db;
    ProcessData data, dt;

    public Server2() {
        // console mode
    }

    public static class sv2 implements Runnable {

        int counter;
        ObjectOutputStream output;
        ObjectInputStream input;
        ServerSocket server;
        Socket client, connection;
        String serverName;
        String type;
        int pos;
        RountingTable rount;
        int currentCircle;
        static String MESSAGE, replyMessage;
        Hashtable hash;
        DataOutputStream out;
        BufferedReader in;
        Database db1, db;
        ProcessData data, dt;

        sv2() {
            //this.q = q; 
            new Thread(this, "sv2").start();
        }

        public void handler(Socket newSocket, String serverName, int pos, int curr, Hashtable hash) {
            client = newSocket;
            this.serverName = serverName;
            rount = new RountingTable();
            this.pos = pos;
            this.currentCircle = curr;
            MESSAGE = "";
            this.hash = hash;
        }

        public void runServer() {
            try {
                String destName = client.getInetAddress().getHostName();
                int destPort = client.getPort();
                System.out.print("Chap nhan ket noi tu " + destName + " tai cong " + destPort + ".\n");
                BufferedReader inStream = new BufferedReader(new InputStreamReader(client.getInputStream()));
                DataOutputStream outStream = new DataOutputStream(client.getOutputStream());
                boolean finished = false;
                {
                    // lay goi tin nhan duoc
                    String inLine = inStream.readLine();
                    MessageProcess re = new MessageProcess(inLine);

                    String st = re.getStart();
                    String je = re.getJeton();
                    String lamport = re.getLamport();
                    String name = re.getServerName();
                    String type = re.getType();
                    String action = re.getAction();
                    String circle = re.getNumCircle();
                    String message = re.getMessage();
                    MESSAGE = message;
                    String jeton;
                    System.out.print("Thong tin nhan duoc :" + "\n" + "start: " + st + "\n" + "jeton: " + je + "\n" + "lamport: " + lamport + "\n" + "servername: " + name + "\n"
                            + "type: " + type + "\n" + "action: " + action + "\n" + "vong dk: " + circle + "\n" + "thong diep: " + message + "\n");
                    int start = Integer.parseInt(st);
                    int act = Integer.parseInt(action);
                    String t = "", rev;

                    if (act == 4) {
                        rev = je;
                        int po = pos + 9;
                        try {
                            rev = je.substring(1, po);
                        } catch (Exception ex) {
                        }
                        t = rev;
                    } else if (act == 3) {
                        try {
                            t = je.substring(0, pos - 1);
                        } catch (Exception ex) {
                        }

                        jeton = je;
                        t += "1";
                        try {
                            t += jeton.substring(pos);
                        } catch (Exception ex) {
                        }
                    } else if (act == 2) {
                        try {
                            t = je.substring(0, pos - 1);
                        } catch (Exception ex) {
                        }

                        jeton = je;
                        t += "1";
                        try {
                            t += jeton.substring(pos);
                        } catch (Exception ex) {
                        }
                    } else if (act == 1) {
                        try {
                            t = je.substring(0, pos - 1);
                        } catch (Exception ex) {
                        }

                        jeton = je;
                        t += "1";
                        try {
                            t += jeton.substring(pos);
                        } catch (Exception ex) {
                        }
                    }
                    int vt = pos;
                    if (vt > rount.max - 1) {
                        vt = 0;
                    }

                    //  xu ly thong tin Synchronymed va ket thuc vong tron ao

                    if (type.equals("Synchronymed") && (start == 4)) {
                        System.out.print("Hoan tat giao dich. Ket thuc vong tron ao.\n\n");
                    }

                    //xu ly thong tin updated va quay vong

                    if (type.equals("Updated") && (start == 4)) {
                        int stt = start;
                        System.out.print("Ket thuc qua trinh cap nhat, kiem tra dong bo hoa TT va Quay vong nguoc.\n\n");
                        stt = 1;
                        act += 1;
                        try {
                            int tam = pos - 2;
                            if (tam < 0) {
                                tam = 2;
                            }
                            if (t.charAt(tam) == '0') {
                                System.out.print("\nServer" + (tam + 1) + " bi su co do jeton nhan duoc la: " + t + ".\n");
                                tam--;
                            }
                            if (tam < 0) {
                                tam = 2;
                            }
                            Connect co = new Connect(rount.table[tam].destination, rount.table[tam].port, rount.table[tam].name);
                            co.connect();
                            String replyServerMessage = "@$" + stt + "|" + t + "|" + lamport + "|" + rount.table[pos - 1].name + "|" + "Synchronymed" + "|" + act + "|" + circle + "$$" + message + "$@";
                            co.requestServer(replyServerMessage);
                            co.shutdown();
                        } catch (Exception Ex) {
                        }

                    }

                    //  xu ly thong tin temped va quay vong

                    if (type.equals("Temped") && (start == 4)) {
                        int stt = start;
                        System.out.print("Ket thuc tao bang tam, cap nhat CSDL chinh Quay vong nguoc.\n\n");
                        stt = 1;
                        act += 1;
                        try {
                            Connect co = new Connect(rount.table[vt].destination, rount.table[vt].port, rount.table[vt].name);
                            co.connect();
                            co.requestServer("@$" + stt + "|" + t + "|" + lamport + "|" + rount.table[pos - 1].name + "|" + "Updated" + "|" + act + "|" + circle + "$$" + message + "$@");
                            co.shutdown();
                        } //bi su co
                        catch (Exception ex) {
                            System.out.print("\n" + rount.table[vt].name + ": bi su co, hien khong lien lac duoc.\n\n");
                            vt++;
                            if (vt > rount.max - 1) {
                                vt = 0;
                            }

                            Connect con = new Connect(rount.table[vt].destination, rount.table[vt].port, rount.table[vt].name);
                            con.connect();
                            con.requestServer("@$" + stt + "|" + t + "|" + lamport + "|" + rount.table[pos - 1].name + "|" + "Updated" + "|" + act + "|" + circle + "$$" + message + "$@");
                            con.shutdown();
                        }
                    }


                    //quay vong nguoc lai cua thong diep locked
                    if (type.equals("Locked") && (start == 4)) {
                        int stt = start;
                        System.out.print("Ket thuc khoa truong du lieu, tao bang tam va Quay vong nguoc.\n\n");
                        stt = 1;
                        act += 1;
                        try {
                            int tam = pos - 2;
                            if (tam < 0) {
                                tam = 2;
                            }
                            if (t.charAt(tam) == '0') {
                                System.out.print("\nServer" + (tam + 1) + " bi su codo jeton nhan duoc la: " + t + ".\n\n");
                                tam--;
                            }
                            if (tam < 0) {
                                tam = 2;
                            }
                            Connect co = new Connect(rount.table[tam].destination, rount.table[tam].port, rount.table[tam].name);
                            co.connect();
                            String replyServerMessage = "@$" + stt + "|" + t + "|" + lamport + "|" + rount.table[pos - 1].name + "|" + "Temped" + "|" + act + "|" + circle + "$$" + message + "$@";
                            co.requestServer(replyServerMessage);
                            co.shutdown();
                        } catch (Exception Ex) {
                        }
                    }

                    //    	 xu ly thong tin tu client
                    if (start == 0) {
                        start++;
                        replyMessage = "Da thuc hien thanh cong.";
                        db1 = new Database();
                        dt = new ProcessData(message);

                        if (message.endsWith("VIEW")) {
                            db1 = new Database();
                            replyMessage = db1.getData();
                        }
                        if ((message.endsWith("SET")) && (!db1.isEmpty(dt.getPos()))) {
                            replyMessage = "Vi tri da co xe goi.";
                        }
                        if ((message.endsWith("DEL")) && (db1.isEmpty(dt.getPos()))) {
                            replyMessage = "Khong co xe tai vi tri nay.";
                        }


                        for (int i = 0; i < replyMessage.length(); ++i) {
                            outStream.write((byte) replyMessage.charAt(i));
                        }
                        System.out.print("Reply: " + replyMessage + "\n");
                        System.out.print("Thuc hien khoa truong DL. Chuyen thong diep.\n\n");
                        try {
                            Connect co = new Connect(rount.table[vt].destination, rount.table[vt].port, rount.table[vt].name);
                            co.connect();
                            co.requestServer("@$" + start + "|" + t + "|" + lamport + "|" + rount.table[pos - 1].name + "|" + "Locked" + "|" + act + "|" + circle + "$$" + message + "$@");
                            co.shutdown();
                        } //bi su co
                        catch (Exception ex) {
                            System.out.print("\n" + rount.table[vt].name + ": bi su co, hien khong lien lac duoc.\n\n");
                            vt++;
                            if (vt > rount.max - 1) {
                                vt = 0;
                            }

                            Connect con = new Connect(rount.table[vt].destination, rount.table[vt].port, rount.table[vt].name);
                            con.connect();
                            con.requestServer("@$" + start + "|" + t + "|" + lamport + "|" + rount.table[pos - 1].name + "|" + "Locked" + "|" + act + "|" + circle + "$$" + message + "$@");
                            con.shutdown();
                        }
                    }

                    // xu ly thong tin locked
                    if (type.equals("Locked") && (start != 4)) {
                        System.out.print("Chuyen thong diep, thuc hien khoa truong DL.\n\n");
                        start++;
                        try {
                            Connect co = new Connect(rount.table[vt].destination, rount.table[vt].port, rount.table[vt].name);
                            co.connect();
                            co.requestServer("@$" + start + "|" + t + "|" + lamport + "|" + rount.table[pos - 1].name + "|" + "Locked" + "|" + action + "|" + circle + "$$" + message + "$@");
                            co.shutdown();
                        } //bi su co
                        catch (Exception ex) {
                            System.out.print("\n" + rount.table[vt].name + ": bi su co, hien khong lien lac duoc.\n\n");
                            vt++;
                            if (vt > rount.max - 1) {
                                vt = 0;
                            }

                            Connect con = new Connect(rount.table[vt].destination, rount.table[vt].port, rount.table[vt].name);
                            con.connect();
                            con.requestServer("@$" + start + "|" + t + "|" + lamport + "|" + rount.table[pos - 1].name + "|" + type + "|" + action + "|" + circle + "$$" + message + "$@");
                            con.shutdown();
                        }
                    }

                    //    	 Xu ly thong diep temp
                    if (type.equals("Temped") && (start != 4)) {
                        System.out.print("Chuyen thong diep, thuc hien tao bang tam CSDL.\n\n");
                        start++;
                        try {
                            int tam = pos - 2;
                            if (tam < 0) {
                                tam = 2;
                            }
                            if (t.charAt(tam) == '0') {
                                System.out.print("\nServer" + (tam + 1) + " bi su co do jeton nhan duoc la: " + t + ".\n\n");
                                tam--;
                            }
                            if (tam < 0) {
                                tam = 2;
                            }
                            Connect co = new Connect(rount.table[tam].destination, rount.table[tam].port, rount.table[tam].name);
                            co.connect();
                            String replyServerMessage = "@$" + start + "|" + t + "|" + lamport + "|" + rount.table[pos - 1].name + "|" + type + "|" + act + "|" + circle + "$$" + message + "$@";
                            co.requestServer(replyServerMessage);
                            co.shutdown();
                        } catch (Exception Ex) {
                        }
                    }

                    //         xu ly thong tin update
                    if (type.equals("Updated") && (start != 4)) {
                        System.out.print("Chuyen thong diep, thuc hien cap nhat bang chinh CSDL.\n\n");
                        start++;
                        try {
                            Connect co = new Connect(rount.table[vt].destination, rount.table[vt].port, rount.table[vt].name);
                            co.connect();
                            co.requestServer("@$" + start + "|" + t + "|" + lamport + "|" + rount.table[pos - 1].name + "|" + type + "|" + action + "|" + circle + "$$" + message + "$@");
                            co.shutdown();
                        } //bi su co
                        catch (Exception ex) {
                            System.out.print("\n" + rount.table[vt].name + ": bi su co, hien khong lien lac duoc.\n\n");
                            vt++;
                            if (vt > rount.max - 1) {
                                vt = 0;
                            }

                            Connect con = new Connect(rount.table[vt].destination, rount.table[vt].port, rount.table[vt].name);
                            con.connect();
                            con.requestServer("@$" + start + "|" + t + "|" + lamport + "|" + rount.table[pos - 1].name + "|" + type + "|" + action + "|" + circle + "$$" + message + "$@");
                            con.shutdown();
                        }
                    }//dong if

                    //      	 Xu ly thong diep synchronym
                    if (type.equals("Synchronymed") && (start != 4)) {
                        System.out.print("Chuyen thong diep, kiem tra qua trinh dong bo hoa cac tien trinh.\n\n");
                        start++;
                        try {
                            int tam = pos - 2;
                            if (tam < 0) {
                                tam = 2;
                            }
                            Connect co = new Connect(rount.table[tam].destination, rount.table[tam].port, rount.table[tam].name);
                            co.connect();
                            String replyServerMessage = "@$" + start + "|" + t + "|" + lamport + "|" + rount.table[pos - 1].name + "|" + type + "|" + action + "|" + circle + "$$" + message + "$@";
                            co.requestServer(replyServerMessage);
                            co.shutdown();
                        } catch (Exception Ex) {
                        }
                    } //dong if

                    outStream.write(13);
                    outStream.write(10);
                    outStream.flush();
                }
            } catch (Exception e) {
            }
        }

        @Override
        public void run() {
            int currentCircle = 0;
            int loop = 1;
            sv2 apps = new sv2();
            Hashtable hash = new Hashtable();
            try {

                GetState gs = new GetState("Server2");
                gs.getCurrentCircle();
                gs.sendUpdate("127.0.0.1", 2003, "Server2");

                ServerSocket server = new ServerSocket(2002);
                while (true) {
                    int localPort = server.getLocalPort();
                    System.out.print("Server 2 is listening on port " + localPort + ".\n");
                    Socket client = server.accept();
                    apps.handler(client, "Server2", 2, currentCircle, hash);
                    apps.runServer();
                    ProcessData data = new ProcessData(MESSAGE);
                    Database db = new Database();
                    boolean ktradb = db.querySQL(data.getPos(), data.getNum(), data.getType(), data.getColor());
                    if (ktradb == true) {
                        if (data.getAct().equalsIgnoreCase("SET")) {
                            db.insertData(data.getPos(), data.getNum(), data.getType(), data.getColor(), data.getTime());
                        } else if (data.getAct().equalsIgnoreCase("DEL")) {
                            db.delData(data.getPos());
                        }
                    }
                    currentCircle++;
                    hash.put(String.valueOf(currentCircle), MESSAGE);

                }
            } catch (IOException e) {
            }
        }
    }

    public static class getLamports implements Runnable {

        /*
         * static variable, dealocate one time and it exist during a program
         */
        getLamports() {
            //lamport = new UDPMulticastServer();
            //lamport.start();
            new Thread(this, "getLamports").start();
        }

        @Override
        public void run() {
            int lp = 0;
            try {
                int i;
                byte[] buffer = new byte[65535];
                byte[] buffer1 = new byte[65535];
                int portM = 5432;
                String lamportS;
                String[] ch = new String[10];
                String[] diachiSV;
                MulticastSocket socketU;
                    boolean bl;
                int max;
//                String address = "224.0.0.0";
//                socketU = new MulticastSocket(portM);
//                InetAddress add = InetAddress.getByName(address);
//                socketU.joinGroup(add);
                while (true) {
                    //System.out.println("Serverf is started...\n");
                    String address = "235.0.0.1";
                    socketU = new MulticastSocket(portM);
                    InetAddress add = InetAddress.getByName(address);
                    socketU.joinGroup(add);

                    System.out.print("dang cho ket noi lay Lamport\n");

                    // Receive request from client
                    DatagramPacket packet = new DatagramPacket(buffer1, buffer1.length);
                    socketU.receive(packet);
                    //check = true;
                    //addres = packet.getAddress().toString();
                    InetAddress client = packet.getAddress();
                    String dataReceive = new String(packet.getData(), 0, packet.getLength());
                    String temp = dataReceive;
                    if (temp.startsWith("#")) {
                        System.out.print("nhan duoc #\n");
                        try {
                            //Send Server
                            String address1 = "235.255.0.1";
                            String address2 = "225.4.5.6";
                            String address3 = "224.0.255.1";
                            String address4 = "224.0.0.0";

                            String addresStr[] = {address, address1, address2, address3, address4};

                            InetAddress add1 = InetAddress.getByName(address1);
                            socketU.joinGroup(add1);
                            InetAddress add2 = InetAddress.getByName(address2);
                            socketU.joinGroup(add2);
                            InetAddress add3 = InetAddress.getByName(address3);
                            socketU.joinGroup(add3);
                            InetAddress add4 = InetAddress.getByName(address4);
                            socketU.joinGroup(add4);
                            InetAddress str[] = {add, add1, add2, add3, add4};

                            //Send information to multiServer
                            for (int j = 1; j < str.length; j++) {
                                String mes = "!RequestLamport-" + address;
                                byte messages[] = mes.getBytes();
                                DatagramPacket packetRS = new DatagramPacket(messages, messages.length, str[j], portM);
                                socketU.send(packetRS);
                                bl = true;
                                while (bl) {
                                    // Receive request from server
                                    DatagramPacket packetReS = new DatagramPacket(buffer1, buffer1.length);
                                    socketU.receive(packetReS);
                                    //check = true;
                                    //addres = packet.getAddress().toString();
                                    //InetAddress sver = packetReS.getAddress();
                                    String messagesS = new String(packetReS.getData(), 0, packetReS.getLength());
                                    ch[j] = messagesS;
                                    if (messagesS.startsWith("!")) {
                                        bl = true;
                                    } else {
                                        bl = false;
                                    }
                                }
                            }

                            max = lp;
                            for (int j = 1; j < str.length; j++) {
                                if (Integer.parseInt(ch[j]) > max) {
                                    max = Integer.parseInt(ch[j]);
                                }
                            }

                            int lamportSendC = max + 1;
                            lp = lamportSendC;

                            for (int j = 1; j < str.length; j++) {
                                String mesLP = Integer.toString(lp);
                                byte messagesLP[] = mesLP.getBytes();
                                DatagramPacket packetSS = new DatagramPacket(messagesLP, messagesLP.length, str[j], portM);
                                socketU.send(packetSS);
                            }

                            //send Client
                            String addressClient = "224.1.2.3";
                            InetAddress addC = InetAddress.getByName(addressClient);

                            String m = Integer.toString(lamportSendC);
                            byte ms[] = m.getBytes();
                            DatagramPacket pkC = new DatagramPacket(ms, ms.length, addC, portM);
                            socketU.send(pkC);
                        } catch (IOException e) {
                            System.out.print("No connect to multiServer");

                        }
                    } else if (temp.startsWith("!")) {
                        System.out.print(lp + "nhan duoc ! \n");
                        diachiSV = temp.split("-");
                        InetAddress addSV = InetAddress.getByName(diachiSV[1]);
//                        System.out.print(diachiSV[1] + "tiep\n");
                        String m = Integer.toString(lp);
                        byte ms[] = m.getBytes();
                        DatagramPacket pkSV = new DatagramPacket(ms, ms.length, addSV, portM);
                        socketU.send(pkSV);
                    } else {
                        lp = Integer.parseInt(temp);
                    }
                    System.out.print(temp + " \n");
                }
            } catch (IOException e) {
                System.out.print("No connect");
            }
        }
    }

    public static void main(String args[]) {
        Hashtable hash = new Hashtable();
        sv2 sv2s = new sv2();
        getLamports lamports = new getLamports();
    }
}