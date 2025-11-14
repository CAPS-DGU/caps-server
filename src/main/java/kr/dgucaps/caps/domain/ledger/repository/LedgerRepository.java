package kr.dgucaps.caps.domain.ledger.repository;

import kr.dgucaps.caps.domain.ledger.entity.Ledger;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface LedgerRepository extends JpaRepository<Ledger, Long> {
    @Modifying
    @Query("UPDATE Ledger l set l.viewCount = l.viewCount + 1 where l.id = :id")
    void updateView(Long id);
}
