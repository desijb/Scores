package net.sijbers.dupas.scores.model.game;

import java.time.LocalDateTime;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.sijbers.dupas.scores.enums.GameStatus;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Game {
	private Long id;
	private GameDay gameDay;
	private Team team1;
	private Team team2;
	private Integer scoreTeam1;
	private Integer scoreTeam2;
	private GameStatus status;
	private String gameId;
	private boolean hasImage;
	@JsonDeserialize(using = LocalDateTimeDeserializer.class)
	@JsonSerialize(using = LocalDateTimeSerializer.class)
	private LocalDateTime gameDateInternal;
}
