package kr.dgucaps.caps.domain.member.repository;

import kr.dgucaps.caps.domain.member.entity.MemberList;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberListRepository extends JpaRepository<MemberList, Long> {
    
    Optional<MemberList> findByStudentIdAndPhoneNumber(String studentId, String phoneNumber);
}