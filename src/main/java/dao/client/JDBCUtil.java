package dao.client;

import utils.EnvConfig;

import java.sql.*;

public class JDBCUtil {
    private static final String HOST = EnvConfig.get("DB_HOST"); // Host Railway cấp
    private static final String PORT = EnvConfig.get("DB_PORT"); // Port Railway cấp
    private static final String DB_NAME = EnvConfig.get("DB_NAME"); // Tên DB
    private static final String USER =EnvConfig.get("DB_USER"); // User Railway cấp
    private static final String PASSWORD = EnvConfig.get("DB_PASSWORD"); // Password Railway cấp

    public static Connection getConnection() {
        Connection conn = null;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            String url = "jdbc:mysql://" + HOST + ":" + PORT + "/" + DB_NAME + "?useSSL=false&serverTimezone=UTC";
            conn = DriverManager.getConnection(url, USER, PASSWORD);
            System.out.println("✅ Kết nối thành công tới Railway MySQL!");
            return conn;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("❌ Không thể kết nối DB", e);
        }
    }

    public static void main(String[] args) {
        Connection c = getConnection();
        System.out.println(c);

    }

}
