package kr.dgucaps.caps.domain.ledger.repository;

import kr.dgucaps.caps.domain.ledger.entity.Ledger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LedgerRepository extends JpaRepository<Ledger, Long> {
    
    @EntityGraph(attributePaths = {"member"})
    @Override
    Page<Ledger> findAll(Pageable pageable);
    
    @EntityGraph(attributePaths = {"member"})
    @Override
    Optional<Ledger> findById(Long id);
    
}