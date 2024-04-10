package net.sijbers.dupas.scores.model.db;

import java.io.Serializable;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.LastModifiedDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.sijbers.dupas.scores.model.AuthSecret;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "secrets")
public class SecretEntity implements Serializable{

	private static final long serialVersionUID = -3107630498786774803L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private Long id;
	
	@Column(name = "creationdate", updatable = false, nullable = false)
	@CreationTimestamp
	private LocalDateTime creationdate;

	@Column(name = "updatedate")
	@LastModifiedDate
	private LocalDateTime updatedate;

	private String secretType;
	private String secret;
	
	public AuthSecret toSecret() {
		AuthSecret authSecret = new AuthSecret();
		authSecret.setSecret(this.getSecret());
		authSecret.setSecretType(this.getSecretType());
		return authSecret;
		
	}
}
