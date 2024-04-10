package net.sijbers.dupas.scores.model.db;

import java.io.Serializable;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.LastModifiedDate;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.sijbers.dupas.scores.enums.GameStatus;
import net.sijbers.dupas.scores.model.game.Game;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

@Slf4j

@Entity
@Table(name = "games")
public class GameEntity implements Serializable{
	private static final long serialVersionUID = 7121976525138678906L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private Long id;

	@Column(name = "creationdate", updatable = false, nullable = false)
	@CreationTimestamp
	private LocalDateTime creationdate;
	
	@Column(name = "updatedate")
	@LastModifiedDate
	private LocalDateTime updatedate;

	private LocalDateTime gamedateInternal;
	
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "team1_id", referencedColumnName = "id")
	private TeamEntity team1;
    
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "team2_id", referencedColumnName = "id")
    private TeamEntity team2;
	
	private Integer scoreTeam1;
	private Integer scoreTeam2;
	private GameStatus status;

	private String gameId;
		    
    @ManyToOne
    @JoinColumn(name="gameday_id", nullable=false)
    private GameDayEntity gameday;
    
    public Game toGame() {
    	Game retVal = new Game();
    	log.debug("jaja: {}", this.getStatus());
    	if (this.getStatus() == null) {
        	//log.info("neenee");
        	return retVal;
    	}
    	retVal.setId(this.getId());
    	//retVal.setSeason(this.getSeason().toSeason());
    	retVal.setStatus(this.getStatus());
    	retVal.setScoreTeam1(this.getScoreTeam1());
    	retVal.setScoreTeam2(this.getScoreTeam2());
    	retVal.setGameDay(this.getGameday().toGameDay());
    	retVal.setTeam1(this.getTeam1().toTeam());
    	retVal.setTeam2(this.getTeam2().toTeam());
    	retVal.setGameId(this.getGameId());
    	retVal.setGameDateInternal(this.getGamedateInternal());
    	return retVal;
    }
}
