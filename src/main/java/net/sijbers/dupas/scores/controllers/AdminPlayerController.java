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
import net.sijbers.dupas.scores.model.StatusMessage;
import net.sijbers.dupas.scores.model.game.Player;
import net.sijbers.dupas.scores.services.AdminService;

@Slf4j
@RestController
@RequestMapping("/api/admin/player")
@Tag(name = "Player Admin API", description = "Player Calls (admin only)")
public class AdminPlayerController {
	
	@Autowired
	AdminService adminService;

	@RequestMapping(value = "/create", method = RequestMethod.POST)
	@Operation(summary = "Create New Player")
	public StatusMessage createPlayer(@RequestBody Player player) {		
		if (adminService.playerIsUnique(player)) { 
			adminService.createPlayer(player);
			return new StatusMessage(0,"player added");
		}
		return new StatusMessage(1,"player already exists");
	}
	
	@RequestMapping(value = "/update", method = RequestMethod.POST)
	@Operation(summary = "Update Player")
	public StatusMessage updatePlayer(@RequestBody Player player) {		
		adminService.updatePlayer(player);
		return new StatusMessage(0,"player updated");
	}
	
	@RequestMapping(value = "/all", method = RequestMethod.GET)
	@Operation(summary = "Get All Players")
	public List<Player> getAllPlayers() {		
		log.debug("getAllPlayers");
		return adminService.getAllPlayers();
	}

	@RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE)
	@Operation(summary = "Delete Player")
	public StatusMessage deletePlayer(@PathVariable(value = "id") long id) {		
		adminService.deletePlayer(id);
		return new StatusMessage(0,"player deleted");
	}
}
