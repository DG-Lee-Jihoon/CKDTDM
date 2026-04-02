package Server2;

import java.io.*;
import java.sql.*;
import java.util.Properties;

public class Database {

    private static final String TABLE = "server2";
    Statement stmt = null;
    ResultSet rs = null;
    Connection conn;

    public Database() {
        Properties p = loadConfig();
        String url  = p.getProperty("db.url",      "jdbc:mysql://localhost:3306/dtdm");
        String user = p.getProperty("db.user",     "root");
        String pass = p.getProperty("db.password", "");
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(url, user, pass);
            stmt = conn.createStatement();
        } catch (Exception ex) {
            System.out.println("[DB-S2] Lỗi kết nối: " + ex.getMessage());
        }
    }

    private Properties loadConfig() {
        Properties p = new Properties();
        try {
            InputStream is = getClass().getResourceAsStream("/config.properties");
            if (is == null) is = new FileInputStream("config.properties");
            p.load(is);
        } catch (Exception e) { }
        return p;
    }

    public void insertData(String vitri, String bienso, String loai, String mau, String gio) {
        String sql = "INSERT INTO " + TABLE + " VALUES (?,?,?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, vitri); ps.setString(2, bienso);
            ps.setString(3, loai);  ps.setString(4, mau);
            ps.setString(5, gio);
            ps.executeUpdate();
        } catch (Exception e) { System.out.println("[DB-S2] insert: " + e.getMessage()); }
    }

    public void delData(String id) {
        String sql = "DELETE FROM " + TABLE + " WHERE vitri=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            ps.executeUpdate();
        } catch (Exception e) { System.out.println("[DB-S2] delete: " + e.getMessage()); }
    }

    public String getData() {
        StringBuilder sb = new StringBuilder();
        try {
            rs = stmt.executeQuery("SELECT * FROM " + TABLE);
            while (rs.next()) {
                sb.append(rs.getString("vitri")).append("|")
                  .append(rs.getString("bienso")).append("|")
                  .append(rs.getString("hieu")).append("|")
                  .append(rs.getString("mau")).append("|")
                  .append(rs.getString("gio")).append("|");
            }
        } catch (Exception e) { }
        return sb.toString();
    }

    public boolean isEmpty(String id) {
        try {
            PreparedStatement ps = conn.prepareStatement("SELECT vitri FROM " + TABLE + " WHERE vitri=?");
            ps.setString(1, id);
            rs = ps.executeQuery();
            return !rs.next();
        } catch (Exception e) { return true; }
    }

    public boolean querySQL(String vitri, String bienso, String hieu, String mau) {
        try {
            PreparedStatement ps = conn.prepareStatement(
                "SELECT * FROM " + TABLE + " WHERE vitri=? AND bienso=? AND hieu=? AND mau=?");
            ps.setString(1, vitri); ps.setString(2, bienso);
            ps.setString(3, hieu);  ps.setString(4, mau);
            rs = ps.executeQuery();
            return !rs.next();
        } catch (Exception e) { return true; }
    }
}
