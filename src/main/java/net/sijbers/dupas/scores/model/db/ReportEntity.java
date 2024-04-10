package net.sijbers.dupas.scores.model.db;

import java.io.Serializable;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.sijbers.dupas.scores.model.game.scoresreport.SavedScoresReport;
import net.sijbers.dupas.scores.model.game.scoresreport.ScoresReport;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

@Slf4j

@Entity
@Table(name = "reports")
public class ReportEntity implements Serializable{
	
	private static final long serialVersionUID = -9006999415785158191L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private Long id;

	@Column(name = "creationdate", updatable = false, nullable = false)
	@CreationTimestamp
	private LocalDateTime creationdate;
	
	@Column(columnDefinition = "TEXT")
	private String reportBlob;
	
    @ManyToOne
    @JoinColumn(name="season_id", nullable=false)
    private SeasonEntity season;
        
    public SavedScoresReport toSavedReport() {
    	SavedScoresReport retVal = new SavedScoresReport();
		ObjectMapper mapper = new ObjectMapper();
		log.debug(this.getReportBlob());
		try {
			retVal = mapper.readValue(this.getReportBlob(), SavedScoresReport.class);
		} catch (JsonProcessingException e) {
			log.error("getPublishedReport: Error parsing reporting blob");
			log.error(e.getLocalizedMessage());
		}
		retVal.setId(this.getId());
		return retVal;
    }
    
    
    public ScoresReport toReport() {
    	ScoresReport retVal = new ScoresReport();
		ObjectMapper mapper = new ObjectMapper();
		log.debug(this.getReportBlob());
		try {
			retVal = mapper.readValue(this.getReportBlob(), ScoresReport.class);
		} catch (JsonProcessingException e) {
			log.error("getPublishedReport: Error parsing reporting blob");
			log.error(e.getLocalizedMessage());
		}
		retVal.setId(this.getId());
		return retVal;
    }
}
