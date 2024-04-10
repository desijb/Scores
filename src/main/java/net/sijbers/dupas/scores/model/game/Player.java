package net.sijbers.dupas.scores.model.game;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.sijbers.dupas.scores.model.db.PlayerEntity;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Player {
	private Long id;
	private String firstName;
	private int partOfTeams;
	private boolean active;	
	private boolean guestPlayer;
	
	public PlayerEntity toRecord() {
		PlayerEntity playerRecord = new PlayerEntity();
		playerRecord.setId(this.getId());
		playerRecord.setFirstName(this.getFirstName());
		//playerRecord.setLastName(this.getLastName());
		playerRecord.setActive(this.isActive());
		playerRecord.setGuestPlayer(this.isGuestPlayer());
		return playerRecord;
	}
}
