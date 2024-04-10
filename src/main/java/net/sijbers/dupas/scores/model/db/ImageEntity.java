package net.sijbers.dupas.scores.model.db;

import java.io.Serializable;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "images")
@Data
@Builder

public class ImageEntity implements Serializable{
	
    private static final long serialVersionUID = -6640960457295977096L;

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

	@Column(name = "creationdate", updatable = false)
	@CreationTimestamp
	private LocalDateTime creationdate;
	
    private String name;
    private String type;

    private String gameId;

    @Lob
    @Column(name = "imagedata", length =  20971520)
    private byte[] imageData;

}
