package kr.dgucaps.caps.domain.report.repository;

import kr.dgucaps.caps.domain.report.entity.ReportFile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportFileRepository extends JpaRepository<ReportFile, Integer> {
}
