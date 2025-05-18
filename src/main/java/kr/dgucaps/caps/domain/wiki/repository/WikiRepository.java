package kr.dgucaps.caps.domain.wiki.repository;

import kr.dgucaps.caps.domain.wiki.entity.Wiki;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WikiRepository extends JpaRepository<Wiki, Long> {

    boolean existsByTitleAndIsDeletedFalse(String title);

    void deleteByTitleAndIsDeletedFalse(String title);

    Optional<Wiki> findByTitleAndIsDeletedFalse(String title);

    List<Wiki> findByTitleOrderByCreatedAtDesc(String title);

    @Query("SELECT w FROM Wiki w WHERE w.isDeleted = false ORDER BY RAND() LIMIT 1")
    Optional<Wiki> findRandomWiki();

    List<Wiki> findFirst10ByIsDeletedFalseOrderByCreatedAtDesc();

    List<Wiki> findByJamoStartsWith(String jamo);

}
