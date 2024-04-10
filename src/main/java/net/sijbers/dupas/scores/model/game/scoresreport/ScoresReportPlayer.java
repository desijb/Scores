package net.sijbers.dupas.scores.model.game.scoresreport;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.sijbers.dupas.scores.model.game.Player;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ScoresReportPlayer {
	
	Player player;
	int wins;
	int loss;
	int totalPoints;
	@JsonIgnore
	int winTokens;
	
	public int incrWins() {
		return this.wins++;
	}

	public int incrLoss() {
		return this.loss++;
	}
	
	public int add2TotalPoints(int points) {
		return totalPoints += points;
	}

	public int getTotalGames() {
		return this.wins + this.loss;
	}
	
	public double getAveragePointsPerGame() {
		if (this.getTotalGames()>0) {
			return (double)this.totalPoints/(double)getTotalGames();
		}		
		return 0;
	}
	
	public double getScore() {
		if (this.getTotalGames()>0) {
			return (((this.wins + Math.floor(this.getTotalGames()/winTokens))*2) - this.loss)/this.getTotalGames();
		}
		return 0;
	}
}
