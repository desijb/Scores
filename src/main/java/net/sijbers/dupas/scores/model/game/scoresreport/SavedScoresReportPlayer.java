package net.sijbers.dupas.scores.model.game.scoresreport;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.sijbers.dupas.scores.model.game.Player;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SavedScoresReportPlayer {
	private Player player;
	private int wins;
	private int loss;
	private int totalPoints;
	private int totalGames;
	private double averagePointsPerGame;
	private double score;
}
