package dao.client;

import utils.EnvConfig;
import java.sql.Connection;
import java.sql.DriverManager;

public class JDBCUtil {

    public static Connection getConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            String host = EnvConfig.get("DB_HOST");
            int port = EnvConfig.getInt("DB_PORT");
            String dbName = EnvConfig.get("DB_NAME");
            String user = EnvConfig.get("DB_USER");
            String pass = EnvConfig.get("DB_PASSWORD");

            String url = "jdbc:mysql://" + host + ":" + port + "/" + dbName
                    + "?useSSL=false&serverTimezone=UTC";

            return DriverManager.getConnection(url, user, pass);

        } catch (Exception e) {
            throw new RuntimeException("❌ Không thể kết nối DB", e);
        }
    }
}
