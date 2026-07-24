package kr.dgucaps.caps.domain.blog.repository;

import kr.dgucaps.caps.domain.blog.entity.BlogImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BlogImageRepository extends JpaRepository<BlogImage, Integer> {

    boolean existsByFileUrl(String fileUrl);
}
