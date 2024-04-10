package net.sijbers.dupas.scores.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import net.sijbers.dupas.scores.model.db.SeasonEntity;

public interface ISeasonRepository  extends JpaRepository<SeasonEntity,Long>{
	List<SeasonEntity>  findByCurrent(boolean current);
}
