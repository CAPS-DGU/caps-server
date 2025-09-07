package kr.dgucaps.caps.domain.member.repository;

import kr.dgucaps.caps.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    Member findByKakaoId(String kakaoId);
}
