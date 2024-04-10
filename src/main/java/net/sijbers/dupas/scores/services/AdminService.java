package net.sijbers.dupas.scores.services;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;
import net.sijbers.dupas.scores.enums.GameStatus;
import net.sijbers.dupas.scores.model.StatusMessage;
import net.sijbers.dupas.scores.model.db.GameDayEntity;
import net.sijbers.dupas.scores.model.db.GameEntity;
import net.sijbers.dupas.scores.model.db.PlayerEntity;
import net.sijbers.dupas.scores.model.db.ReportEntity;
import net.sijbers.dupas.scores.model.db.SeasonEntity;
import net.sijbers.dupas.scores.model.db.TeamEntity;
import net.sijbers.dupas.scores.model.game.Game;
import net.sijbers.dupas.scores.model.game.GameDay;
import net.sijbers.dupas.scores.model.game.Player;
import net.sijbers.dupas.scores.model.game.Season;
import net.sijbers.dupas.scores.model.game.Team;
import net.sijbers.dupas.scores.model.game.playerreport.PlayerReport;
import net.sijbers.dupas.scores.model.game.scoresreport.SavedScoresReport;
import net.sijbers.dupas.scores.model.game.scoresreport.SavedScoresReportPlayer;
import net.sijbers.dupas.scores.model.game.scoresreport.ScoresReport;
import net.sijbers.dupas.scores.model.game.scoresreport.ScoresReportPlayer;
import net.sijbers.dupas.scores.repositories.IGameDayRepository;
import net.sijbers.dupas.scores.repositories.IGameRepository;
import net.sijbers.dupas.scores.repositories.IPlayerRepository;
import net.sijbers.dupas.scores.repositories.ITeamRepository;
import net.sijbers.dupas.scores.repositories.IReportRepository;
import net.sijbers.dupas.scores.repositories.ISeasonRepository;

@Slf4j
@Service("AdminService")
public class AdminService {
	
	@Autowired
	IPlayerRepository playerRepository;
	
	@Autowired
	IGameRepository gameRepository;
	
	@Autowired	
	ISeasonRepository seasonRepository;
	
	@Autowired
	ITeamRepository teamRepository;
	
	@Autowired
	IReportRepository reportRepository;

	@Autowired
	IGameDayRepository gameDayRepository;
	
	@Autowired
	FilesStorageServiceImpl storageService;
	
	@Autowired	
	SecretService secretService;
		
	@Value("${game.wintokens}")
	private int winTokens;	

	@EventListener(ApplicationReadyEvent.class)
	public void doSomethingAfterStartup() {
		checkGuestPlayer();
		secretService.checkSecrets();
		storageService.init();
	}
	
	public TeamEntity createTeam(Team team) {
		TeamEntity teamRecord = new TeamEntity();
		if ((team.getPlayers() == null)||(team.getPlayers().isEmpty())) {
			return null;
		}
		teamRecord.setPlayers(new HashSet<PlayerEntity>() );
		for (Player player: team.getPlayers()) {
			Optional<PlayerEntity> playerOptional = playerRepository.findById(player.getId());
			if (playerOptional.isEmpty()) {
				return null;
			}
			teamRecord.getPlayers().add(playerOptional.get());
		}
		return teamRepository.save(teamRecord);
	}
	
	private String serialiseObject(Object logObject ) {
		ObjectMapper mapper = new ObjectMapper();
		String returnValue = "";
		try {
			returnValue = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(logObject);
			log.debug(returnValue);
		} catch (JsonProcessingException e) {
			log.error(e.getMessage());	
		}
		return returnValue;
	}
		
	public PlayerReport playerReport(long seasonid, long id) {
		
		PlayerReport playerReport = new PlayerReport();
		
		ScoresReport scoreReport = scoreCalculation(seasonid);

		
		scoreReport.getPlayerReports().forEach(playerLine -> {
			if (playerLine.getPlayer().getId() == id) {
				playerReport.setScoresReportPlayer(new SavedScoresReportPlayer(
						playerLine.getPlayer(),
						playerLine.getWins(),
						playerLine.getLoss(),
						playerLine.getTotalPoints(),
						playerLine.getTotalGames(),
						playerLine.getAveragePointsPerGame(),
						playerLine.getScore()));
				
			}
		});
		
		return playerReport;
	}

	private void removeEmptyGameDays(SeasonEntity currentSeason) {
		//delete gamedays without games
		List<Long> toDelete = new ArrayList<>();
		for (GameDayEntity gameDayRecord: gameDayRepository.findBySequenceNotAndSeasonOrderByGamedateAsc(-1,currentSeason)) {
			if (gameDayRecord.getGames().isEmpty()) {
				toDelete.add(gameDayRecord.getGameday_id());
			}
		}
		for (Long id:toDelete) {
			gameDayRepository.deleteById(id);
		}
	}
	
	private void renumberGameDays(SeasonEntity currentSeason) {
		//redo sequyence of gamedays
		int counter=1;
		log.debug("start renumber");
		for (GameDayEntity gameDayRecord: gameDayRepository.findBySeasonOrderByGamedateAsc(currentSeason)) {
			if ((gameDayRecord.getSequence() == null)||(gameDayRecord.getSequence() != -1)) {
				log.debug("number: {}", counter);
				gameDayRecord.setSequence(counter++);
				gameDayRepository.save(gameDayRecord);
			}
		}
	}
	
	private GameDayEntity getGameDay(LocalDateTime gameDate, SeasonEntity currentSeason) {
		GameDayEntity retVal = new GameDayEntity();
		for (GameDayEntity gameDayRecord: gameDayRepository.findBySequenceNotAndSeasonOrderByGamedateAsc(-1,currentSeason)) {
			if (( gameDate.isAfter(gameDayRecord.getGamedate().minusHours(6)))&&(gameDate.isBefore(gameDayRecord.getGamedate().plusHours(12)))) {
				retVal = gameDayRecord;
			}
		}
		
		if (retVal.getCreationdate() == null) {
			retVal.setGamedate(gameDate);
			retVal.setSeason(currentSeason);
			gameDayRepository.save(retVal);
//			renumberGameDays(currentSeason);
		}
		return retVal;
	}
	
	private GameDayEntity getBogusGameDay(SeasonEntity currentSeason) {
		//this method will return a "bogus" gameday that links games  with their season
		// sequence will be -1, for the fronted to know not to display it.
		// only in case of approval a "real" gameday will be assigned.
		
		//check if the bogus gameday exists, if not create it.
		
		List<GameDayEntity> gameDays = gameDayRepository.findBySequence(-1);
		if (gameDays.size()==1) {
			return gameDays.get(0);
		}
		
		GameDayEntity gameDayRecord = new GameDayEntity();
		gameDayRecord.setSequence(-1);
		gameDayRecord.setSeason(currentSeason);
		gameDayRepository.save(gameDayRecord);
		return gameDayRecord;
	}
	
	public GameEntity createGame(Game game) {
		if ((game.getTeam1() == null)||
			(game.getTeam2() == null)) {
			return  null;
		}
		log.debug("start create game");
		log.debug(serialiseObject(game));
		
		GameEntity gameRecord = new GameEntity();
		SeasonEntity currentSeason = getCurrentSeasonRecord();
		
		//todo: assign gamdeday on approval, not on creation
		//until approval assign bogus gameday
		GameDayEntity gameDayEntity = getBogusGameDay(currentSeason);
		gameRecord.setGameday(gameDayEntity);

		gameRecord.setGamedateInternal(game.getGameDateInternal());
		TeamEntity team1Record = createTeam(game.getTeam1());
		TeamEntity team2Record = createTeam(game.getTeam2());
		gameRecord.setGameId(game.getGameId());
		
		if ((team1Record == null)||(team2Record == null)) {
			return null;
		}
		
		gameRecord.setTeam1(team1Record);
		gameRecord.setTeam2(team2Record);
		
		if (game.getScoreTeam1() != null) {
			gameRecord.setScoreTeam1(game.getScoreTeam1());
		}
		if (game.getScoreTeam2() != null) {
			gameRecord.setScoreTeam2(game.getScoreTeam2());
		}
		gameRecord.setStatus(GameStatus.nieuw);
		
		return gameRepository.save(gameRecord);
	}
	
	public Game changeGameStatus(Game game) {
		return changeGameStatus(game.getId(),game.getStatus());
	}
	 	
	public Game changeGameStatus(long gameId,GameStatus status) {
		//in case of any unapproved  status -> set gameday to bogus gameday
				
		Optional<GameEntity> gameOptional = gameRepository.findById(gameId);
		if (gameOptional.isEmpty()) {
			return null;//todo: fix cause this wiull throw an expception
		}
		GameEntity gameRecord = gameOptional.get();
		gameRecord.setStatus(status);
		switch(status) {
		case approved:
			gameRecord.setGameday(getGameDay(gameRecord.getGamedateInternal(),gameRecord.getGameday().getSeason()));
			break;
		case nieuw:
			gameRecord.setGameday(getBogusGameDay(gameRecord.getGameday().getSeason()));
			break;
		case rejected:
			gameRecord.setGameday(getBogusGameDay(gameRecord.getGameday().getSeason()));
			break;
		case deleted:
			gameRecord.setGameday(getBogusGameDay(gameRecord.getGameday().getSeason()));
			break;			
		}
	//	if (status == GameStatus.deleted) {
	//		gameRepository.deleteById(gameId);
			//todo: check gamedays and delete empty gamedays
			//todo: delete image from db and disk
	//		return gameRecord;
	//	}
		gameRepository.save(gameRecord);
		
		//now check if there are gamedays without approved games; delete those
		removeEmptyGameDays(gameRecord.getGameday().getSeason());
		// then renumber the gamedays
		renumberGameDays(gameRecord.getGameday().getSeason());
		
		Game game = gameRecord.toGame();
		game.setHasImage(storageService.hasImage(game.getGameId()));
		return game;
	}
	
	public boolean playerIsUnique(Player player) {
		for (PlayerEntity playerRecord:playerRepository.findAll()) {
			if ((playerRecord.getFirstName().equalsIgnoreCase(player.getFirstName())))	{
				return false;
			}
		}
		return true;
	}
	
	private void checkGuestPlayer() {
		log.debug("check guest player");
		//make sure there is at least a guest player
		if (playerRepository.findByGuestPlayer(true).size() == 0) {
			PlayerEntity playerRecord = new PlayerEntity();
			playerRecord.setFirstName("Gast");
			//playerRecord.setLastName("");
			playerRecord.setActive(true);
			playerRecord.setGuestPlayer(true);
			playerRepository.save(playerRecord);	
			log.info("guest account created");
		}
	}
	
	public List<Game> getAllGames4GameDay(long gameDayId) {
		List<Game> games = new ArrayList<>();
		Optional<GameDayEntity> gameDayRecord = gameDayRepository.findById(gameDayId);
		if (gameDayRecord.isPresent()) {
			for (GameEntity gameRecord: gameRepository.findByGameday(gameDayRecord.get())) {
				games.add(gameRecord.toGame());
			}
		}
		return games;
	}
	
	public List<GameDay> getGameDays(long seasonId) {
		List<GameDay> gameDays = new ArrayList<>();
		Optional<SeasonEntity> seasonRecord = seasonRepository.findById(seasonId);
		if (seasonRecord.isPresent()) {			
			for (GameDayEntity gameDayRecord: gameDayRepository.findBySequenceNotAndSeasonOrderByGamedateAsc(-1,seasonRecord.get())) {
				gameDays.add(gameDayRecord.toGameDay());
			}
		}
		return gameDays;
	}
	
	public List<Player> getAllPlayers() {
		List<Player> players = new ArrayList<>();
		for (PlayerEntity playerRecord:playerRepository.findAll()) {
			players.add(playerRecord.toPlayer());
		}
		return players;
	}
	
	public List<Player> getActivePlayers() {
		List<Player> players = new ArrayList<>();
		for (PlayerEntity playerRecord:playerRepository.findByActive(true)) {
			players.add(playerRecord.toPlayer());
		}
		return players;
	}

	public PlayerEntity createPlayer(Player player) {
		player.setGuestPlayer(false);
		return playerRepository.save(player.toRecord());
	}
	
	public boolean deletePlayer(long id) {
		Optional<PlayerEntity> playerRecords = playerRepository.findById(id);
		if (playerRecords.isEmpty()) {
			return false;
		}
		PlayerEntity playerRecord = playerRecords.get();		
		if ((! playerRecord.isGuestPlayer())&&(playerRecord.getTeams().isEmpty())) {
			playerRepository.deleteById(id);
		}
		return true;
	}
	
	public PlayerEntity updatePlayer(Player player) {
		log.debug("start update");
		Optional<PlayerEntity> playerRecords = playerRepository.findById(player.getId());
		if (playerRecords.isEmpty()) {
			return null;
		}
		PlayerEntity playerRecord = playerRecords.get();
		if (! playerRecord.isGuestPlayer()) {
			playerRepository.save(player.toRecord());	
		}
		return playerRecord;
	}
	
	private void populatePlayersReport(ScoresReport report) {
		//get all players
		List<Player> players = getAllPlayers();
		for (Player player:players) {
			ScoresReportPlayer  reportPlayer= new ScoresReportPlayer(player,0,0,0,winTokens);
			report.getPlayerReportsHash().put(player.getId(),reportPlayer);
		}
	}
	
	private void updateWinners(ScoresReport report, Team team,int score) {
		team.getPlayers().forEach(player -> {
			ScoresReportPlayer reportPlayer = report.getPlayerReportsHash().get(player.getId());
			reportPlayer.incrWins();
			reportPlayer.add2TotalPoints(score);
		});
	}

	private void updateLosers(ScoresReport report, Team team,int score) {
		team.getPlayers().forEach(player -> {
			ScoresReportPlayer reportPlayer = report.getPlayerReportsHash().get(player.getId());
			reportPlayer.incrLoss();
			reportPlayer.add2TotalPoints(score);
			
		});
	}
	
	public StatusMessage publishReport(SavedScoresReport report) {
		ReportEntity reportRecord = new ReportEntity();
		reportRecord.setSeason(report.getSeason().toRecord());
		ObjectMapper mapper = new ObjectMapper();
		try {
			reportRecord.setReportBlob(mapper.writeValueAsString(report));
			reportRepository.save(reportRecord);
			return new StatusMessage(0,"ScoresReport Published");
		} catch (JsonProcessingException e) {
			log.error("publishReport: Error saving report");
		}
		return new StatusMessage(2,"Saving ScoresReport Failed");				
	}

	public StatusMessage publishReport(long seasonId, int kut) {
		ReportEntity reportRecord = new ReportEntity();
		SeasonEntity seasonRecord = new SeasonEntity();
		Optional<SeasonEntity> seasonRecords = seasonRepository.findById(seasonId);
		if (seasonRecords.isPresent()) {
			seasonRecord = seasonRecords.get();
			reportRecord.setSeason(seasonRecord);
		}
		
		ScoresReport report = scoreCalculation(seasonId);
		report.setKut(kut);
		
		ObjectMapper mapper = new ObjectMapper();
		try {
			reportRecord.setReportBlob(mapper.writeValueAsString(report));
			reportRepository.save(reportRecord);
			return new StatusMessage(0,"ScoresReport Published");
		} catch (JsonProcessingException e) {
			log.error("publishReport: Error saving report");
		}
		return new StatusMessage(2,"Saving ScoresReport Failed");				
	}

	public StatusMessage deleteReport(long id) {
		reportRepository.deleteById(id);
		return new StatusMessage(0,"ScoresReport Deleted");				
	}
	
	public SavedScoresReport getPublishedReport(long id) {
		Optional<ReportEntity> reportRecordOptional = reportRepository.findById(id);
		if (reportRecordOptional.isPresent()) {
			ReportEntity reportRecord = reportRecordOptional.get();
			SavedScoresReport report = reportRecord.toSavedReport();
			return report;
		}
		return new SavedScoresReport();
	}
	
	public List<SavedScoresReport> getPublishedReports(long seasonId) {
		List<SavedScoresReport> retVal = new ArrayList<>();
		SeasonEntity seasonRecord = new SeasonEntity();
		Optional<SeasonEntity> seasonRecords = seasonRepository.findById(seasonId);
		if (seasonRecords.isPresent()) {
			seasonRecord = seasonRecords.get();
			reportRepository.findBySeason(seasonRecord).forEach(reportRecord -> {
				retVal.add(reportRecord.toSavedReport());
			});
		}
		return retVal;
	}

	public ScoresReport scoreCalculation(long seasonId) {
		//find the latest ganeday for this season
		GameDayEntity gameDayRecord = new GameDayEntity();
		Optional<GameDayEntity> gameDayRecords = Optional.of(gameDayRepository.getLatestGameday4Season(seasonId));
		if (gameDayRecords.isPresent()) {
			gameDayRecord = gameDayRecords.get();
		}
		log.debug("gameday id: {}", gameDayRecord.getGameday_id());
		return scoreCalculation(seasonId, gameDayRecord.getSequence());
	}

	public ScoresReport scoreCalculation(long seasonId, int gameDaySequence) {
		ScoresReport report = new ScoresReport();
		
		report.setReportDate(LocalDateTime.now(ZoneOffset.UTC));
		log.debug("local: {}",LocalDateTime.now(ZoneOffset.UTC));
		log.debug("gameDaySequence: {}",gameDaySequence);
		
		populatePlayersReport(report);
		// get the season record
		SeasonEntity seasonRecord = new SeasonEntity();
		Optional<SeasonEntity> seasonRecords = seasonRepository.findById(seasonId);
		if (seasonRecords.isPresent()) {
			seasonRecord = seasonRecords.get();
			report.setSeason(seasonRecord.toSeason());
		}
		//get all games for the season
		List<Game> games = getAllGames4Season(seasonRecord.toSeason(),GameStatus.approved,gameDaySequence);
		for (Game game:games) {
			log.debug("game: {}", game.getId());

			
			Team winners = new Team();
			Team losers = new Team();
			int winningScore = 0;
			int losingScore = 0;
			
			if (game.getScoreTeam1()>game.getScoreTeam2()) {
				winners = game.getTeam1();
				winningScore = game.getScoreTeam1();

				losers = game.getTeam2();
				losingScore = game.getScoreTeam2();

			}
			else {
				winners = game.getTeam2();
				winningScore = game.getScoreTeam2();

				losers = game.getTeam1();				
				losingScore = game.getScoreTeam1();
			}
			//update report values
			report.incrTotalGames();
			
			report.add2TotalPoints(game.getScoreTeam1());
			report.add2TotalPoints(game.getScoreTeam2());

			report.add2WinningPoints(winningScore);
			report.add2LosingPoints(losingScore);
			updateWinners(report, winners, winningScore );
			updateLosers(report, losers, losingScore );
		}
		return report;
	}
	
	public List<Game> recentGames() {
		List<Game>  retVal = new ArrayList<>();
		
		LocalDateTime fromDate = LocalDateTime.now().plusDays(-8);
		
		List<SeasonEntity> seasonRecords = seasonRepository.findByCurrent(true);
		if (seasonRecords.size() == 1) {
			List<GameEntity> gameRecords = gameRepository.findByGameday_SeasonAndGamedateInternalGreaterThanEqualAndStatusNot(seasonRecords.get(0),fromDate,GameStatus.deleted);
			for (GameEntity gameRecord:gameRecords) {
				Game game = gameRecord.toGame();
				game.setHasImage(false);
				if (storageService.hasImage(game.getGameId())) {
					game.setHasImage(true);
				}
				retVal.add(game);
			}
		}
		return retVal;		
	}
	
	public List<Game> getAllGames4Season(Season season) {
		List<Game>  retVal = new ArrayList<>();
		Optional<SeasonEntity> seasonRecords = seasonRepository.findById(season.getSeasonId());
		if (seasonRecords.isPresent()) {
			SeasonEntity seasonRecord = seasonRecords.get();
			List<GameEntity> gameRecords = gameRepository.findByGameday_SeasonAndStatusNot(seasonRecord,GameStatus.deleted);
			for (GameEntity gameRecord:gameRecords) {
				Game game = gameRecord.toGame();
				//todo switch to check on db
				game.setHasImage(false);
				if (storageService.hasImage(game.getGameId())) {
					game.setHasImage(true);
				}
				retVal.add(game);
			}
		}
		return retVal;		
	}
	
	public List<Game> getAllGames4Season(Season season, GameStatus status) {		
		log.debug("status: {}",status);
		return getAllGames4Season(season, status, LocalDateTime.now());
	}
	
	public List<Game> getAllGames4Season(Season season, GameStatus status, int gameDaySequence) {
		SeasonEntity seasonRecord = new SeasonEntity();
		Optional<SeasonEntity> seasonRecords = seasonRepository.findById(season.getSeasonId());
		if (seasonRecords.isPresent()) {
			seasonRecord = seasonRecords.get();
			List<GameDayEntity> gameDayRecords = gameDayRepository.findBySequenceAndSeason(gameDaySequence, seasonRecord);
			if (gameDayRecords.size()==1) {
				GameDayEntity gameDayRecord = gameDayRecords.get(0);
				return getAllGames4Season(season, status, gameDayRecord.getGamedate());
			}
		}		
		return new ArrayList<Game>();
	}
	
	public List<Game> getAllGames4Season(Season season, GameStatus status, LocalDateTime beforeDate) {
		List<Game>  retVal = new ArrayList<>();
		Optional<SeasonEntity> seasonRecords = seasonRepository.findById(season.getSeasonId());
		if (seasonRecords.isPresent()) {
			SeasonEntity seasonRecord = seasonRecords.get();
			
			log.debug("beforeDate: {}", beforeDate );
			
			//translate gamestarus to number to support native query
			int gameStatusid = 0;
			switch(status) {
				case approved:
					gameStatusid = 0;
					break;
				case nieuw:
					gameStatusid = 1;
					break;
				case rejected:
					gameStatusid = 2;
					break;
				case deleted:
					gameStatusid = 3;
					break;				
			}
			
			List<GameEntity> gameRecords = gameRepository.findGamesBeforeDateWithStatus(beforeDate, seasonRecord.getSeasonId(), gameStatusid);
			for (GameEntity gameRecord:gameRecords) {
				Game game = gameRecord.toGame();
				game.setHasImage(storageService.hasImage(game.getGameId()));
				retVal.add(game);
			}
		}
		return retVal;		
	}

	public List<Game> getAllGames4Season(long id) {
		Optional<SeasonEntity> seasonRecords = seasonRepository.findById(id);
		if (seasonRecords.isPresent()) {
			return getAllGames4Season(seasonRecords.get().toSeason());
		}
		return new ArrayList<>();		
	}

	public List<Game> getAllGames4Season(long id, GameStatus status) {
		Optional<SeasonEntity> seasonRecords = seasonRepository.findById(id);
		if (seasonRecords.isPresent()) {
			return getAllGames4Season(seasonRecords.get().toSeason(),status);
		}
		return new ArrayList<>();		
	}

	public List<Game> getAllGames4CurrentSeason() {
		List<SeasonEntity> seasonRecords = seasonRepository.findByCurrent(true);
		if (seasonRecords.size() == 1) {
			return getAllGames4Season(seasonRecords.get(0).toSeason());
		}
		return new ArrayList<>();
	}

	public boolean seasonIsUnique(Season season) {
		for (SeasonEntity seasonRecord:seasonRepository.findAll()) {
			if (seasonRecord.getLabel().equalsIgnoreCase(season.getLabel())) 	{
				return false;
			}
		}
		return true;
	}
	
	public Season getCurrentSeason() {
		List<SeasonEntity> seasonRecords = seasonRepository.findByCurrent(true);
		if (seasonRecords.size() == 1) {
			SeasonEntity seasonRecord = seasonRecords.get(0);
			return seasonRecord.toSeason();
		}
		return new Season();		
	}
	
	private SeasonEntity getCurrentSeasonRecord() {
		List<SeasonEntity> seasonRecords = seasonRepository.findByCurrent(true);
		if (seasonRecords.size() == 1) {
			SeasonEntity seasonRecord = seasonRecords.get(0);
			return seasonRecord;
		}
		return null;		
	}

	public SeasonEntity createSeason(Season season) {
		if (season.isCurrent()) {setAllSeasonsNotCurrent(); }

		log.debug("current: {}",season.isCurrent());
		return seasonRepository.save(season.toRecord());
	}
	
	private void setAllSeasonsNotCurrent() {
		for (SeasonEntity seasonRecord: seasonRepository.findAll()) {
			seasonRecord.setCurrent(false);
			seasonRepository.save(seasonRecord);	
		}
	}

	public List<Season> getAllSeasons() {
		List<Season> seasons = new ArrayList<>();
		for (SeasonEntity seasonRecord: seasonRepository.findAll()) {
			seasons.add(seasonRecord.toSeason());
		}
		return seasons;
	}
	
	public SeasonEntity updateSeason(Season season) {
		Optional<SeasonEntity> seasonRecords = seasonRepository.findById(season.getSeasonId());
		if (seasonRecords.isEmpty()) {
			return null;
		}
		
		if (season.isCurrent()) {setAllSeasonsNotCurrent(); }

		SeasonEntity seasonRecord = seasonRecords.get();
		seasonRecord.setLabel(season.getLabel());
		seasonRecord.setCurrent(season.isCurrent());
		seasonRepository.save(seasonRecord);	
		return seasonRecord;
	}

	public boolean deleteSeason(long id) {
		Optional<SeasonEntity> seasonRecords = seasonRepository.findById(id);
		if (seasonRecords.isEmpty()) {
			return false;
		}
		SeasonEntity seasonRecord = seasonRecords.get();
		if (seasonRecord.toSeason().isGamesPlayed()) {
			return false;
		}
		seasonRepository.deleteById(id);
		return true;
	}
}
