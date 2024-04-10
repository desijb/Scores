package net.sijbers.dupas.scores.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import net.sijbers.dupas.scores.model.db.ReportEntity;
import net.sijbers.dupas.scores.model.db.SeasonEntity;

public interface IReportRepository extends JpaRepository<ReportEntity,Long> {
	List<ReportEntity> findBySeason(SeasonEntity season);
}
