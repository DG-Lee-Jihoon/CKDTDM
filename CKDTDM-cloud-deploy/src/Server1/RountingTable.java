package Server1;

import java.io.*;
import java.util.Properties;

/**
 * Bảng định tuyến vòng tròn ảo - đọc IP từ config.properties
 * Thứ tự vòng: S1 -> S2 -> S3 -> S4 -> S5 (chiều thuận)
 * Pha ngược:   S1 <- S2 <- S3 <- S4 <- S5
 */
public class RountingTable {

    public VirtualCircle table[];
    public int max = 5;

    public RountingTable() {
        table = new VirtualCircle[5];
        Properties props = loadConfig();

        for (int i = 1; i <= 5; i++) {
            String host = props.getProperty("server" + i + ".host", "127.0.0.1");
            int port = Integer.parseInt(props.getProperty("server" + i + ".port", String.valueOf(2000 + i)));
            table[i - 1] = new VirtualCircle(host, port, "Server" + i);
        }
        max = 5;
    }

    private Properties loadConfig() {
        Properties p = new Properties();
        try {
            InputStream is = getClass().getResourceAsStream("/config.properties");
            if (is == null) is = new FileInputStream("config.properties");
            p.load(is);
        } catch (Exception e) {
            System.out.println("[RountingTable] Dùng IP mặc định 127.0.0.1");
        }
        return p;
    }
}
