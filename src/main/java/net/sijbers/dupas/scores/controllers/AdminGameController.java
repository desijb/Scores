package net.sijbers.dupas.scores.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import net.sijbers.dupas.scores.enums.GameStatus;
import net.sijbers.dupas.scores.model.game.Game;
import net.sijbers.dupas.scores.model.game.Season;
import net.sijbers.dupas.scores.services.AdminService;

@Slf4j
@RestController
@RequestMapping("/api/admin/games")
@Tag(name = "Games Admin API", description = "Games Calls (admin only)")
public class AdminGameController {
	
	@Autowired
	AdminService adminService;

	@RequestMapping(value = "/season", method = RequestMethod.POST)
	@Operation(summary = "get games 4 season")
	public List<Game> getAllGames4Season(@RequestBody Season season) {	
		log.debug("getAllGames4Season");
		return adminService.getAllGames4Season(season);
	}
	
	@RequestMapping(value = "/season/{id}", method = RequestMethod.GET)
	@Operation(summary = "get games 4 season with id")
	public List<Game> getAllGames4Season(@PathVariable(value = "id") long id) {		
		return adminService.getAllGames4Season(id);
	}
	
	@RequestMapping(value = "/season/{id}/{status}", method = RequestMethod.GET)
	@Operation(summary = "get games 4 season with id and status")
	public List<Game> getAllGames4Season(
			@PathVariable(value = "id") long id,
			@PathVariable(value = "status") GameStatus status) {		
		return adminService.getAllGames4Season(id,status);
	}
	
	@RequestMapping(value = "/gameday/{id}", method = RequestMethod.GET)
	@Operation(summary = "get games 4 gameday")
	public List<Game> getAllGames4GameDay(
			@PathVariable(value = "id") long id) {		
		return adminService.getAllGames4GameDay(id);
	}

	@RequestMapping(value = "/gamestatus/{id}/{status}", method = RequestMethod.GET)
	@Operation(summary = "update Game by id with status")
	public Game updateGame(
			@PathVariable(value = "id") long id,
			@PathVariable(value = "status") GameStatus status) {	
		return adminService.changeGameStatus(id,status);
	}
}
