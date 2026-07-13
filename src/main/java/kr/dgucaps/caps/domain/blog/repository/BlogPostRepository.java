package kr.dgucaps.caps.domain.blog.repository;

import kr.dgucaps.caps.domain.blog.entity.BlogCategory;
import kr.dgucaps.caps.domain.blog.entity.BlogPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface BlogPostRepository extends JpaRepository<BlogPost, Integer> {

    @EntityGraph(attributePaths = {"member"})
    Page<BlogPost> findByIsPrivateFalse(Pageable pageable);

    @EntityGraph(attributePaths = {"member"})
    Page<BlogPost> findByCategoryAndIsPrivateFalse(BlogCategory category, Pageable pageable);

    @EntityGraph(attributePaths = {"member"})
    Page<BlogPost> findByCategory(BlogCategory category, Pageable pageable);

    @EntityGraph(attributePaths = {"member"})
    @Query("SELECT b FROM BlogPost b WHERE b.id = :id")
    Optional<BlogPost> findWithDetailsById(@Param("id") Integer id);
}
