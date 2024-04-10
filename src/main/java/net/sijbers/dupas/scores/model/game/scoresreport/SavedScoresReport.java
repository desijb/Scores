package net.sijbers.dupas.scores.model.game.scoresreport;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
public class SavedScoresReport {
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
	public List<SavedScoresReportPlayer>  playerReports = new ArrayList<>();

}
