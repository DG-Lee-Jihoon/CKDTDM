/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Server2;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.*;

public class Database {

    String drivername = "com.mysql.jdbc.Driver";
    String connectionURL = "jdbc:mysql://localhost:3306/dtdm";
    String username = "root";
    String password = "";
    Statement stmt = null;
    ResultSet rs = null;
    Connection conn;

    public Database() {
        try {
            Class.forName(drivername).newInstance();
            conn = DriverManager.getConnection(connectionURL, username, password);
            stmt = conn.createStatement();
        } catch (Exception ex) {
            System.out.println("SQLException: " + ex.getMessage());
        }
    }

    public void insertData(String vitri, String bienso, String loai, String mau, String gio) {
        String sSQL = "INSERT INTO server2 VALUES ('" + vitri + "','" + bienso + "','" + loai + "','" + mau + "','" + gio + "')";
        try {
            stmt.executeUpdate(sSQL);
        } catch (Exception e) {
            System.out.println("SQLException: " + e.getMessage());
        }
    }

    public void delData(String id) {
        try {
            String sSQL = "DELETE FROM server2 WHERE vitri='" + id + "'";
            stmt.executeUpdate(sSQL);
        } catch (Exception e) {
            System.out.println("SQLException: " + e.getMessage());
        }
    }

    public String getData() {
        String pos, num, type, clr, time, st = "";
        try {
            String sSQL = "SELECT * FROM server2";
            rs = stmt.executeQuery(sSQL);
            while (rs.next()) {
                pos = rs.getString("vitri");
                num = rs.getString("bienso");
                type = rs.getString("hieu");
                clr = rs.getString("mau");
                time = rs.getString("gio");
                st = st + pos + "|" + num + "|" + type + "|" + clr + "|" + time + "|";
            }
        } catch (Exception e) {
        }
        return st;
    }

    public boolean isEmpty(String id) {
        boolean check = true;
        try {
            String sSQL = "SELECT vitri FROM server2 WHERE vitri='" + id + "'";
            rs = stmt.executeQuery(sSQL);
            if (rs.next()) {
                check = false;
            }
        } catch (Exception e) {
        }
        return check;
    }

    public boolean querySQL(String vitri, String bienso, String hieu, String mau) {
        boolean check = true;
        try {
            //ProcessData data = new ProcessData(ms);

            String sSQL = "SELECT * FROM server2 WHERE vitri='" + vitri + "'"
                    + "AND bienso='" + bienso + "'"
                    + "AND hieu='" + hieu + "'"
                    + "AND mau='" + mau + "'";
            rs = stmt.executeQuery(sSQL);
            //ResultSetMetaData rsmd = rs.getMetaData();
            //int colnum = rsmd.getColumnCount();
            if (rs != null && rs.next()) {
                check = false;
            }
        } catch (Exception e) {
        }
        return check;
    }
}