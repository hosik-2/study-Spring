package hello.jdbc.service;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV2;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * 트랜잭션 - 파라미터 연동, 풀을 고려한 종료
 */
@Slf4j
@RequiredArgsConstructor
public class MemberServiceV2 {

    private final DataSource dataSource; //서비스에서(여기서) 커넥션을 얻어야 하기 때문에 이게 필요함
    private final MemberRepositoryV2 memberRepository;

    public void accountTransfer(String fromId, String toId, int money) throws SQLException {
        Connection con = dataSource.getConnection();
        try {
            con.setAutoCommit(false); //트랜잭션 시작 그 다음 비즈니스 로직 시작
            //비즈니스 로직
            bizLogic(con, fromId, toId, money);
            con.commit(); // 여기까지 정상적으로 수행될 경우 커밋
            //커밋, 롤백
        } catch (Exception e) {
            con.rollback(); // 실패시 롤백
            throw new IllegalStateException(e);
        } finally {
            release(con); // Statement나 ResultSet은 리포지토리에서 반납한다
            // 즉 자원을 부른 곳에서 반납을 해야 하기 떄문에 커넥션은 여기서 반납한다
        }

    }

    private void bizLogic(Connection con, String fromId, String toId, int money) throws SQLException {
        Member fromMember = memberRepository.findById(con, fromId);
        Member toMember = memberRepository.findById(con, toId);

        memberRepository.update(con, fromId, fromMember.getMoney() - money);
        validation(toMember);
        memberRepository.update(con, toId, toMember.getMoney() + money);
    }

    private static void release(Connection con) {
        if (con != null) {
            try {
                con.setAutoCommit(true); // 이 커넥션이 풀로 돌아가기 전에 원래 상태로 돌려주기
                // 어떤 메서드나 다른 로직에서는 필요 없어질 수 있기 때문
            } catch (Exception e) {
                log.info("error", e);
            }
        }
    }

    private static void validation(Member toMember) {
        if (toMember.getMemberId().equals("ex")) {
            throw new IllegalStateException("이체중 예외 발생");
        }
    }


}
