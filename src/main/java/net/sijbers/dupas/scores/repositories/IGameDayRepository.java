package net.sijbers.dupas.scores.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import net.sijbers.dupas.scores.model.db.GameDayEntity;
import net.sijbers.dupas.scores.model.db.SeasonEntity;

public interface IGameDayRepository extends JpaRepository<GameDayEntity,Long>{
	
//	 @Query(
//	            value = "SELECT max(sequence) FROM gamedays", 
//	            nativeQuery=true
//	        )
//	 public Integer getLastSeqeunce();
	 
	 List<GameDayEntity> findBySequence(int sequence);
	 List<GameDayEntity> findBySeasonOrderByGamedateAsc(SeasonEntity season);
	 List<GameDayEntity> findBySequenceNotAndSeasonOrderByGamedateAsc(Integer sequence,SeasonEntity season);

	 List<GameDayEntity> findBySequenceAndSeason(Integer sequence,SeasonEntity season);

	 
		@Query(
	            value = "select * from gamedays where sequence = (select max(sequence) from gamedays where season_id= :season)",
	            nativeQuery=true
				)

		GameDayEntity getLatestGameday4Season(@Param("season") long season);
	 
}
