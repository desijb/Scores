package net.sijbers.dupas.scores.repositories;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import net.sijbers.dupas.scores.enums.GameStatus;
import net.sijbers.dupas.scores.model.db.GameDayEntity;
import net.sijbers.dupas.scores.model.db.GameEntity;
import net.sijbers.dupas.scores.model.db.SeasonEntity;

public interface IGameRepository  extends JpaRepository<GameEntity, Long>{ 
	List<GameEntity> findByGameday_SeasonAndStatusNot(SeasonEntity season, GameStatus status);
	List<GameEntity> findByGameday_SeasonAndStatus(SeasonEntity season, GameStatus status);
	List<GameEntity> findByGameday_SeasonAndGamedateInternalGreaterThanEqualAndStatusNot(SeasonEntity season,LocalDateTime date, GameStatus status);
	List<GameEntity> findByGameday(GameDayEntity gameDay);	 
	 
	@Query(
            value = "    select "
            		+ "        g1_0.id, "
            		+ "        g1_0.creationdate, "
            		+ "        g1_0.game_id, "
            		+ "        g1_0.gamedate_internal, "
            		+ "        g1_0.gameday_id, "
            		+ "        g1_0.score_team1, "
            		+ "        g1_0.score_team2, "
            		+ "        g1_0.status, "
            		+ "        g1_0.team1_id, "
            		+ "        g1_0.team2_id, "
            		+ "        g1_0.updatedate "
            		+ "    from "
            		+ "        games g1_0 "
            		+ "    left join "
            		+ "        gamedays g2_0 "
            		+ "            on g2_0.gameday_id=g1_0.gameday_id "
            		+ "    left join "
            		+ "        seasons s1_0 "
            		+ "            on s1_0.season_id=g2_0.season_id "
            		+ "    where "
            		+ "        s1_0.season_id= :season "
            		+ "        and g1_0.status= :status "
            		+ "        and (g2_0.gamedate <= :beforeDate or g2_0.gamedate is null)", 
            nativeQuery=true
			)
	List<GameEntity>  findGamesBeforeDateWithStatus(@Param("beforeDate") LocalDateTime beforeDate, @Param("season") long season, @Param("status") int status);
	
}
