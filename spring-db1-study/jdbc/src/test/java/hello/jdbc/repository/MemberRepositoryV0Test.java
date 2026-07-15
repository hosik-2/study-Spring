package hello.jdbc.repository;


import hello.jdbc.domain.Member;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class MemberRepositoryV0Test {

    MemberRepositoryV0 repository = new MemberRepositoryV0();

    @Test
    void crud() throws SQLException {
        //save
        Member member = new Member("memberV3", 10000);
        repository.save(member);

        //findById
        Member findMember = repository.findById(member.getMemberId());
        log.info("findMember={}", findMember);
        assertThat(findMember).isEqualTo(member);

        //update - money: 10000 -> 20000
        repository.update(member.getMemberId(), 20000);
        Member updatedMember = repository.findById(member.getMemberId());
        assertThat(updatedMember).isEqualTo(new Member("memberV3", 20000));
        assertThat(updatedMember.getMoney()).isEqualTo(20000);

        //delete
        repository.delete(member.getMemberId());
        assertThatThrownBy(() -> repository.findById(member.getMemberId()))
                .isInstanceOf(NoSuchElementException.class);
        // 쉽게 얘기해서 ThrownBy니까 던졌다 무언가를 <- 이뜻으로 이해하고 파라미터 안에는 예외가 "던져질" 로직을 적고
        // 그 예외가 무슨 예외냐?를 isInstanceOf()에 인자를 예외 클래스 정보를 써주면 된다!
        // 그리고 왜 NoSuchElementException 이게 나오냐면 우리가 findById 메서드에 rs.netx()가 false가 나올 시
        // else 문에다가 NoSuchElementException을 던지게 만들었기 때문임
    }

}