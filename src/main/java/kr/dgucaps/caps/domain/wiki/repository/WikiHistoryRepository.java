package kr.dgucaps.caps.domain.wiki.repository;

import kr.dgucaps.caps.domain.wiki.entity.WikiHistory;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WikiHistoryRepository extends JpaRepository<WikiHistory, Long> {

    @EntityGraph(attributePaths = {"member"})
    List<WikiHistory> findByTitleOrderByCreatedAtDesc(String title);
}
