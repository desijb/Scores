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
import net.sijbers.dupas.scores.model.game.Season;
import net.sijbers.dupas.scores.services.AdminService;

@Slf4j
@RestController
@RequestMapping("/api/admin/season")
@Tag(name = "Season Admin API", description = "Season Calls (admin only)")
public class AdminSeasonController {
	
	@Autowired
	AdminService adminService;

	@RequestMapping(value = "/create", method = RequestMethod.POST)
	@Operation(summary = "Create New season")
	public StatusMessage createSeason(@RequestBody Season season) {		
		if (adminService.seasonIsUnique(season)) { 
			adminService.createSeason(season);
			return new StatusMessage(0,"season added");
		}
		return new StatusMessage(1,"season already exists");
	}
	
	@RequestMapping(value = "/update", method = RequestMethod.POST)
	@Operation(summary = "Update season")
	public StatusMessage updateSeason(@RequestBody Season season) {		
		adminService.updateSeason(season);
		return new StatusMessage(0,"season updated");
	}
	
	@RequestMapping(value = "/all", method = RequestMethod.GET)
	@Operation(summary = "Get All seasons")
	public List<Season> getAllSeasons() {	
		return adminService.getAllSeasons();
	}

	@RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE)
	@Operation(summary = "Delete season")
	public StatusMessage deleteSeason(@PathVariable(value = "id") long id) {	
		log.debug("delete season");
		adminService.deleteSeason(id);
		return new StatusMessage(0,"season deleted");
	}
}
