package hello.jdbc.repository;

import hello.jdbc.connection.ConnectionConst;
import hello.jdbc.connection.DBConnectionUtil;
import hello.jdbc.domain.Member;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;

import java.sql.*;
import java.util.NoSuchElementException;

/**
 * JDBC - DriverManager 사용
 */

@Slf4j
public class MemberRepositoryV0 {

    public Member save(Member member) throws SQLException { // try-catch에서 나온 예외 던지기!
        String sql = "insert into member(member_id, money) values (?, ?)";

        Connection con = null;
        PreparedStatement pstmt = null; // 이걸 가지고 DB에 쿼리를 날릴 수 있음

        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql); // 커넥션에 연결된 정보를 바탕으로 statement세팅
            /**
             * 앞으로 커밋, 수정, 롤백 등 모든 것은 커넥션으로 한다고 함. DB와 연결된 통로이기 때문(연결 정보가 있음)
             * 그리고 pstmt는 statement의 종류로 미리 준비해놓는(일반 statement는 보안 및 효율 안좋음) 것, 파라미터를 바인딩 할 수 있음
             * 즉, 저렇게 쿼리를 먼저 세팅하고 대기시켜놓은 다음 ?에 해당하는 벨류만 나중에 수정해서 올릴 수 있당
             */
            pstmt.setString(1, member.getMemberId()); // ?에다가 넣는 겁니다
            pstmt.setInt(2, member.getMoney());
            pstmt.executeUpdate(); // 준비해둔 설정을 반영해서 SQL 실행!
            return member; // 객체 반환 -> member(member_id, money)
        } catch (SQLException e) {
            log.info("db error", e);
            throw e; // 발생하는 예외를 여기서 처리하는 것이 아닌 밖으로 던지기
        } finally { // 예외가 나든 말든 커넥션과 pstmt 자원 닫기
            close(con, pstmt, null);
        }

    }

    public Member findById(String memberId) throws SQLException {
        String sql = "select * from member where member_id = ?";

        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, memberId); // 파라미터로 받은 memberId값을 넘겨서 쿼리 세팅

            rs = pstmt.executeQuery();// Query는 select, Update는 데이터를 변경할 때 쓰는 것!
            if (rs.next()) { //cursor가 처음엔 데이터가 없는 첫 빈공간에 가있음 이걸 한 번 넘겨주는 것임
                //rs.next() -> 다음으로 넘겼을 때 boolean 값을 반환함
                Member member = new Member(); // DB응답을 받을 객체 생성
                member.setMemberId(rs.getString("member_id")); //ResultSet에 저장된 DB응답 결과 빼오기
                member.setMoney(rs.getInt("money"));
                return member;
            } else {
                throw new NoSuchElementException("member not found memberId=" + memberId);
            }

        } catch (SQLException e) {
            log.info("db error", e);
            throw e;
        } finally {
            close(con, pstmt, rs);
        }

    }

    public void update(String memberId, int money) throws SQLException {
        String sql = "update member set money=? where member_id=?";

        Connection con = null;
        PreparedStatement pstmt = null;

        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setInt(1, money);
            pstmt.setString(2, memberId);
            int resultSize = pstmt.executeUpdate(); // executeUpdate()는 영향을 받은 행(row)의 갯수를 반환해줌(int)
            log.info("resultSize={}", resultSize);

        } catch (SQLException e) {
            log.info("db error", e);
            throw e;
        } finally {
            close(con, pstmt, null);
        }

    }

    public void delete(String memberId) throws SQLException {
        String sql = "delete from member where member_id=?";

        Connection con = null;
        PreparedStatement pstmt = null;

        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, memberId);
            pstmt.executeUpdate(); // executeUpdate()는 영향을 받은 행(row)의 갯수를 반환해줌(int)

        } catch (SQLException e) {
            log.info("db error", e);
            throw e;
        } finally {
            close(con, pstmt, null);
        }
    }

    private void close(Connection con, Statement stmt, ResultSet rs) {
        // 역순으로 닫는 겁니다. con -> pstmt // pstmt.close -> con.close
        // 근데 여기서 예외가 터지면 밑에 con.close가 호출이 안됨
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                log.info("error", e);
            }
        }

        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException e) {
                log.info("error", e);
            }
        }

        if (con != null) {
            try {
                con.close();
            } catch (SQLException e) {
                log.info("error", e);
            }
        }


    }

    @NonNull
    private static Connection getConnection() {
        return DBConnectionUtil.getConnection();
    }


}
