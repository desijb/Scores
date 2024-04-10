package net.sijbers.dupas.scores.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import net.sijbers.dupas.scores.model.StatusMessage;
import net.sijbers.dupas.scores.model.game.Game;
import net.sijbers.dupas.scores.model.game.GameDay;
import net.sijbers.dupas.scores.model.game.Player;
import net.sijbers.dupas.scores.model.game.Season;
import net.sijbers.dupas.scores.model.game.scoresreport.SavedScoresReport;
import net.sijbers.dupas.scores.services.AdminService;
import net.sijbers.dupas.scores.services.FilesStorageService;

@Slf4j
@RestController
@RequestMapping("/api/player")
@Tag(name = "Player Calls API", description = "Calls for players (players&admin) ")
public class PlayerController {

	@Autowired
	AdminService adminService;

	@Autowired
	FilesStorageService storageService;

	@RequestMapping(value = "/role", method = RequestMethod.GET)
	@Operation(summary = "check role")
	public StatusMessage checkRole() {
		log.debug("check player role");
		return new StatusMessage(0, "player");
	}

	@RequestMapping(value = "/season/current", method = RequestMethod.GET)
	@Operation(summary = "get current season")
	public Season getCurrentSeason() {
		return adminService.getCurrentSeason();
	}

	@RequestMapping(value = "/players/active", method = RequestMethod.GET)
	@Operation(summary = "get active players")
	public List<Player> getActivePlayers() {
		return adminService.getActivePlayers();
	}

	@RequestMapping(value = "/game/new", method = RequestMethod.POST)
	@Operation(summary = "create new game")
	public StatusMessage createGame(@RequestBody Game game) {
		adminService.createGame(game);
		return new StatusMessage(0, "Game Added");
	}
	
	@RequestMapping(value = "/game/last", method = RequestMethod.GET)
	@Operation(summary = "get last week games")
	public List<Game> lastWeekGames() {
		return adminService.recentGames();
	}

	@RequestMapping(value = "/report/get/{seasonId}", method = RequestMethod.GET)
	@Operation(summary = "get reports for season")
	public List<SavedScoresReport> getReports(@PathVariable(value = "seasonId") long seasonId) {
		return adminService.getPublishedReports(seasonId);
	}
	
	@RequestMapping(value = "/gameday/get/{seasonId}", method = RequestMethod.GET)
	@Operation(summary = "get gamedays for season")
	public List<GameDay> getGameDays(@PathVariable(value = "seasonId") long seasonId) {
		return adminService.getGameDays(seasonId);
	}

	@RequestMapping(value = "/report/one/{reportId}", method = RequestMethod.GET)
	@Operation(summary = "get one report for season")
	public SavedScoresReport getOneReport(@PathVariable(value = "reportId") long reportId) {
		return adminService.getPublishedReport(reportId);
	}

	@RequestMapping(value = "/seasons", method = RequestMethod.GET)
	@Operation(summary = "Get All seasons")
	public List<Season> getAllSeasons() {
		return adminService.getAllSeasons();
	}

	@RequestMapping(value = "/image/upload/{gameId}", method = RequestMethod.POST)
	public ResponseEntity<StatusMessage> uploadFile(@RequestParam("file") MultipartFile file,
			@PathVariable(value = "gameId") String gameId) {
		String message = "";
		try {
			storageService.save(file, gameId);

			message = "Uploaded the file successfully: " + file.getOriginalFilename();
			return ResponseEntity.status(HttpStatus.OK).body(new StatusMessage(0, message));
		} catch (Exception e) {
			message = "Could not upload the file: " + file.getOriginalFilename() + ". Error: " + e.getMessage();
			return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new StatusMessage(1, message));
		}
	}
	
	@RequestMapping(value = "/image/delete/{gameId}", method = RequestMethod.DELETE)
	public ResponseEntity<StatusMessage> deleteFiles(
			@PathVariable(value = "gameId") String gameId) {
		try {
			storageService.delete(gameId);

			String message = "Deleted for " + gameId;
			return ResponseEntity.status(HttpStatus.OK).body(new StatusMessage(0, message));
		} catch (Exception e) {
			String message = "Could not delete the file for : " + gameId+ ". Error: " + e.getMessage();
			return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new StatusMessage(1, message));
		}
	}
	
	@RequestMapping(value = "/image/image/{gameId}", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<byte[]> downloadImage(@PathVariable(value = "gameId") String gameId) {
		byte[] image = storageService.getImage(gameId);
		return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.valueOf("image/png")).body(image);
	}

}
