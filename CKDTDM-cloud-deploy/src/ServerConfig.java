import java.io.*;
import java.util.Properties;

/**
 * Đọc cấu hình IP/Port từ file config.properties
 * Dùng chung cho tất cả Server1..5
 */
public class ServerConfig {

    private static Properties props = new Properties();
    private static boolean loaded = false;

    public static void load() {
        if (loaded) return;
        try {
            InputStream is = ServerConfig.class.getResourceAsStream("/config.properties");
            if (is == null) {
                // thử đọc từ thư mục hiện tại
                is = new FileInputStream("config.properties");
            }
            props.load(is);
            loaded = true;
        } catch (Exception e) {
            System.out.println("[Config] Không tìm thấy config.properties, dùng giá trị mặc định 127.0.0.1");
        }
    }

    public static String getHost(int serverNum) {
        load();
        return props.getProperty("server" + serverNum + ".host", "127.0.0.1");
    }

    public static int getPort(int serverNum) {
        load();
        String val = props.getProperty("server" + serverNum + ".port", String.valueOf(2000 + serverNum));
        return Integer.parseInt(val);
    }

    public static String getDbUrl() {
        load();
        return props.getProperty("db.url", "jdbc:mysql://localhost:3306/dtdm");
    }

    public static String getDbUser() {
        load();
        return props.getProperty("db.user", "root");
    }

    public static String getDbPassword() {
        load();
        return props.getProperty("db.password", "");
    }
}
