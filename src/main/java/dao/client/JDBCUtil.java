package dao.client;

import utils.EnvConfig;

import java.sql.*;

public class JDBCUtil {
    private static final String HOST = EnvConfig.get("DB_HOST");
    private static final String PORT = EnvConfig.get("DB_PORT");
    private static final String DB_NAME = EnvConfig.get("DB_NAME");
    private static final String USER = EnvConfig.get("DB_USER");
    private static final String PASSWORD = EnvConfig.get("DB_PASSWORD");

    public static Connection getConnection() {
        Connection conn = null;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            // Xây dựng URL với SSL cho TiDB Cloud
            String url = "jdbc:mysql://" + HOST + ":" + PORT + "/" + DB_NAME 
                + "?sslMode=VERIFY_IDENTITY"
                + "&enabledTLSProtocols=TLSv1.2,TLSv1.3"
                + "&tlsCertificates=system"
                + "&useSSL=true"
                + "&requireSSL=true"
                + "&serverTimezone=UTC";
            
            conn = DriverManager.getConnection(url, USER, PASSWORD);
            System.out.println("✅ Kết nối thành công tới TiDB Cloud!");
            return conn;
            
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("❌ Không thể kết nối TiDB Cloud", e);
        }
    }

    public static void main(String[] args) {
        Connection c = getConnection();
        System.out.println(c);
    }
}
