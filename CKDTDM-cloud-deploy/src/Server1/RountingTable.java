package Server1;

import java.io.*;
import java.util.Properties;

public class RountingTable {

    public VirtualCircle table[];
    public int max = 4;

    public RountingTable() {
        table = new VirtualCircle[4];
        Properties props = loadConfig();
        for (int i = 1; i <= 4; i++) {
            String host = props.getProperty("server" + i + ".host", "127.0.0.1");
            int port = Integer.parseInt(props.getProperty("server" + i + ".port", String.valueOf(2000 + i)));
            table[i - 1] = new VirtualCircle(host, port, "Server" + i);
        }
        max = 4;
    }

    private Properties loadConfig() {
        Properties p = new Properties();
        try {
            InputStream is = getClass().getResourceAsStream("/config.properties");
            if (is == null) is = new FileInputStream("config.properties");
            p.load(is);
        } catch (Exception e) {
            System.out.println("[RountingTable] Dung IP mac dinh 127.0.0.1");
        }
        return p;
    }
}
