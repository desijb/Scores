package net.sijbers.dupas.scores.model.db;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;

import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.LastModifiedDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.sijbers.dupas.scores.model.game.Player;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "players")
public class PlayerEntity implements Serializable{
	private static final long serialVersionUID = 2137176989425630806L;
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private Long id;
	
	@Column(name = "creationdate", updatable = false, nullable = false)
	@CreationTimestamp
	private LocalDateTime creationdate;
	
	@Column(name = "updatedate")
	@LastModifiedDate
	private LocalDateTime updatedate;


	private String firstName;
	
	private boolean active;
	private boolean guestPlayer;
		
	@ManyToMany(fetch = FetchType.EAGER,mappedBy = "players")
	private Set<TeamEntity> teams;
	
	public Player toPlayer() {
		Player player = new Player();
		player.setId(this.getId());
		player.setActive(this.isActive());
		player.setFirstName(this.getFirstName());
		player.setGuestPlayer(this.isGuestPlayer());
		player.setPartOfTeams(this.getTeams().size());
		return player;
	}
}
