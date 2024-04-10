package net.sijbers.dupas.scores.model.db;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.sijbers.dupas.scores.model.game.Team;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "teams")
public class TeamEntity implements Serializable {

	private static final long serialVersionUID = -4958089329661141273L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private Long id;

	@Column(name = "creationdate", updatable = false, nullable = false)
	@CreationTimestamp
	private LocalDateTime creationdate;

	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "team_players_link", joinColumns = @JoinColumn(name = "team_id"), inverseJoinColumns = @JoinColumn(name = "player_id"))
	private Set<PlayerEntity> players;

	public Team toTeam() {
		Team team = new Team();
		team.setId(this.getId());
		this.getPlayers().forEach(player -> {
			team.getPlayers().add(player.toPlayer());
		});
		return team;
	}

}
