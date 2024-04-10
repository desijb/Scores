package net.sijbers.dupas.scores.model.game;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Team {
	private Long id;
	private List<Player> players = new ArrayList<>();

}
