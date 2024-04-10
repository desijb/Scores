package net.sijbers.dupas.scores.model.db;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;

import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.LastModifiedDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.sijbers.dupas.scores.model.game.Season;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "seasons")
public class SeasonEntity implements Serializable{
	
	private static final long serialVersionUID = -3318649388029598783L;
	@Id
	@Column(name = "season_id")
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private Long seasonId;
	
	@Column(name = "creationdate", updatable = false, nullable = false)
	@CreationTimestamp
	private LocalDateTime creationdate;
	
	@Column(name = "updatedate")
	@LastModifiedDate
	private LocalDateTime updatedate;
	
	private String label;
	
	private boolean current;
	    
    @OneToMany(mappedBy="season")
    private Set<GameDayEntity> gameDays;
    
    @OneToMany(mappedBy="season")
    private Set<ReportEntity> reports;
    
    public Season toSeason() {
    	Season season = new Season();
    	season.setCurrent(this.isCurrent());
    	season.setLabel(this.getLabel());
    	season.setSeasonId(this.getSeasonId());
    	if (this.gameDays.size()>0) {//if there are gamedays, there are probably also games
    		season.setGamesPlayed(true);
    	}
    	else {
    		season.setGamesPlayed(false);    		
    	}
    	return season;
    }
}
