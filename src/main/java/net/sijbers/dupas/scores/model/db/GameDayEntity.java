package net.sijbers.dupas.scores.model.db;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.sijbers.dupas.scores.model.game.GameDay;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

@Slf4j

@Entity
@Table(name = "gamedays")
public class GameDayEntity implements Serializable{
	private static final long serialVersionUID = 1999105529653416821L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private Long gameday_id;

	@Column(name = "creationdate", updatable = false, nullable = false)
	@CreationTimestamp
	private LocalDateTime creationdate;
	
	private Integer sequence;
	private LocalDateTime gamedate;

    @OneToMany(mappedBy="gameday")
    private Set<GameEntity> games;
    
    @ManyToOne
    @JoinColumn(name="season_id", nullable=false)
    private SeasonEntity season;
    
    public GameDay toGameDay() {
    	log.debug("toGameDay");
    	GameDay gameDay = new GameDay();
    	gameDay.setGamedate(this.getGamedate());
    	gameDay.setGameDateId(this.getGameday_id());
    	gameDay.setSequence(this.getSequence());    
    	if (this.getGames() == null ) { //in case of new gameday, there games are not yet linked to this record and therefore can be null
        	gameDay.setNumberOfGamesPlayed(0);
    	}
    	else {
        	gameDay.setNumberOfGamesPlayed(this.getGames().size());    		
    	}
    	gameDay.setSeason(this.getSeason().toSeason());
    	return gameDay;
    }
}
