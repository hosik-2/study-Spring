package hello.jdbc.connection;

import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static hello.jdbc.connection.ConnectionConst.*;

@Slf4j
public class DBConnectionUtil {

    public static Connection getConnection() {
        try {
            Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            // 커넥션을 가져오려면 드라이버매니저에 있는 거 불러다 쓰세요
            // DB맞춰서 알아서 드라이버매니저가 가져올 거임
            log.info("get connection={}, class={}", connection, connection.getClass());
            return connection; // 당연히 커넥션을 받으려는 거니까 반환을 해줘야 겠죠?
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }

}
