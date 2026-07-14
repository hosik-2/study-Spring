package hello.jdbc.connection;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.sql.Connection;

@Slf4j
public class DBConnectionUtilTest {

    @Test
    void connection() {
        Connection connection = DBConnectionUtil.getConnection();
        Assertions.assertThat(connection).isNotNull();
        // 21:51:42.590 [Test worker] INFO hello.jdbc.connection.DBConnectionUtil --
        // get connection=conn0: url=jdbc:h2:tcp://localhost/~/test user=SA, class=class org.h2.jdbc.JdbcConnection
        // 이게 결과임 클래스 정보를 보면 h2라고 적혀져 있음 그리고 커넥션 연결 정보도 뜸 (이름 등)
    }


}
