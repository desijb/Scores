package net.sijbers.dupas.scores.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import net.sijbers.dupas.scores.model.StatusMessage;
import net.sijbers.dupas.scores.model.game.playerreport.PlayerReport;
import net.sijbers.dupas.scores.model.game.scoresreport.SavedScoresReport;
import net.sijbers.dupas.scores.model.game.scoresreport.ScoresReport;
import net.sijbers.dupas.scores.services.AdminService;

@RestController
@RequestMapping("/api/admin/score")
@Tag(name = "Score Admin API", description = "Score Calls (admin only)")
public class AdminScoreController {
	
	@Autowired
	AdminService adminService;

	@RequestMapping(value = "/{seasonid}", method = RequestMethod.GET)
	@Operation(summary = "Calculate scores")
	public ScoresReport scoreCalculation(
			@PathVariable(value = "seasonid") long seasonid) {	
		return adminService.scoreCalculation(seasonid);
	}	

	@RequestMapping(value = "/{seasonid}/{gameDaySequence}", method = RequestMethod.GET)
	@Operation(summary = "Calculate scores for Gameday")
	public ScoresReport scoreCalculation(
			@PathVariable(value = "seasonid") long seasonid, 
			@PathVariable(value = "gameDaySequence") int gameDaySequence) {	
		return adminService.scoreCalculation(seasonid,gameDaySequence);
	}	

	@RequestMapping(value = "/report/publish", method = RequestMethod.POST)
	@Operation(summary = "publish report")
	public StatusMessage publishReport(
			@RequestBody SavedScoresReport report) {	
		return adminService.publishReport(report);
	}
		
	@RequestMapping(value = "/report/publish/{seasonId}/{cut}", method = RequestMethod.GET)
	@Operation(summary = "publish report")
	public StatusMessage publishReport(
			@PathVariable(value = "seasonId") long seasonId,
			@PathVariable(value = "cut") int cut) {	
		return adminService.publishReport(seasonId,cut);
	}

	@RequestMapping(value = "/report/delete/{reportId}", method = RequestMethod.DELETE)
	@Operation(summary = "delete report")
	public StatusMessage deleteReport(
			@PathVariable(value = "reportId") long reportId) {	
		return adminService.deleteReport(reportId);
	}
	
	@RequestMapping(value = "/report/player/{seasonId}/{id}", method = RequestMethod.GET)
	@Operation(summary = "player report")
	public PlayerReport playerReport(
			@PathVariable(value = "seasonId") long seasonId,
			@PathVariable(value = "id") long id) {	
		return adminService.playerReport(seasonId, id);
	}

}
