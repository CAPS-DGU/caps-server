package kr.dgucaps.caps.domain.wiki.repository;

import kr.dgucaps.caps.domain.wiki.entity.Wiki;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WikiRepository extends JpaRepository<Wiki, Long> {

    @EntityGraph(attributePaths = {"member"})
    Optional<Wiki> findByTitle(String title);

    @Query("SELECT w FROM Wiki w ORDER BY RAND() LIMIT 1")
    Optional<Wiki> findRandomWiki();

    List<Wiki> findFirst10ByOrderByUpdatedAtDesc();

    List<Wiki> findByJamoStartsWith(String jamo);

    boolean existsByTitle(String title);
}
