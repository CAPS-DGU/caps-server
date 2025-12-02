package kr.dgucaps.caps.domain.ledger.repository;

import kr.dgucaps.caps.domain.ledger.entity.Ledger;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LedgerRepository extends JpaRepository<Ledger, Long> {
}