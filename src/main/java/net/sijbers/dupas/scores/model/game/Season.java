package net.sijbers.dupas.scores.model.game;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.sijbers.dupas.scores.model.db.SeasonEntity;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Season {
	private Long seasonId;
	private String label;
	private boolean gamesPlayed;
	private boolean current;
	
	public SeasonEntity toRecord() {
		SeasonEntity seasonRecord = new SeasonEntity();
		seasonRecord.setSeasonId(this.getSeasonId());
		seasonRecord.setLabel(this.getLabel());
		seasonRecord.setCurrent(this.isCurrent());
		return seasonRecord;
	}
}
