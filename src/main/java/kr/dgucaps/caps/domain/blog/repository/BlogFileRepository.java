package kr.dgucaps.caps.domain.blog.repository;

import kr.dgucaps.caps.domain.blog.entity.BlogFile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BlogFileRepository extends JpaRepository<BlogFile, Integer> {
}
