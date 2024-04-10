package net.sijbers.dupas.scores.model.game.scoresreport;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.sijbers.dupas.scores.model.game.Season;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ScoresReport {
	private Long id;
	@JsonDeserialize(using = LocalDateTimeDeserializer.class)
	@JsonSerialize(using = LocalDateTimeSerializer.class)
	private LocalDateTime reportDate;
	private int totalGames = 0;
	private int totalPoints = 0;
	private int totalWinningPoints = 0;
	private int totalLosingPoints = 0;
	private int kut=0;
	private Season season;
	@JsonIgnore
	private Map<Long,ScoresReportPlayer> playerReportsHash = new HashMap<Long,ScoresReportPlayer>();
	
	public int incrTotalGames() {
		return this.totalGames++;
	}
	
	public int add2TotalPoints(int points) {
		return this.totalPoints += points;
	}

	public int add2WinningPoints(int points) {
		return this.totalWinningPoints += points;
	}

	public int add2LosingPoints(int points) {
		return this.totalLosingPoints += points;
	}
	
	public List<ScoresReportPlayer>  getPlayerReports() {
		List<ScoresReportPlayer> retVal = new ArrayList<>();
		this.getPlayerReportsHash().forEach((key,value) -> {
			//remove inactive olayers with zero games
			if ((value.getPlayer().isActive())||(value.getTotalGames()>0)) {
				retVal.add(value);
			}
		});
		return retVal;
	}
}
