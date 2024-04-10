package net.sijbers.dupas.scores.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import net.sijbers.dupas.scores.model.AuthSecret;
import net.sijbers.dupas.scores.model.StatusMessage;
import net.sijbers.dupas.scores.model.db.SecretEntity;
import net.sijbers.dupas.scores.repositories.ISecretRepository;

@Slf4j
@Service("SecretService")
public class SecretService {
	
	@Autowired
	ISecretRepository secretRepository;
	
	public void checkSecrets() {
		log.info("check secrets");
		//make sure there are at least default secrets
		if (secretRepository.count()==0) {
			SecretEntity adminSecret = new SecretEntity();
			adminSecret.setSecretType("admin");
			adminSecret.setSecret("admin");
			SecretEntity playerSecret = new SecretEntity();
			playerSecret.setSecretType("player");
			playerSecret.setSecret("player");			
			secretRepository.save(adminSecret);
			secretRepository.save(playerSecret);
			log.info("default secrets created");			
		}
	}
	
	public List<AuthSecret> getAllSecrets() {
		List<AuthSecret> secrets = new ArrayList<>();
		for (SecretEntity secretRecord: secretRepository.findAll()) {
			secrets.add(secretRecord.toSecret());
		}
		return secrets;
	}
	
	private boolean updateSecret(String secretType,String secret) {
		List<SecretEntity> records = secretRepository.findBySecretType(secretType);
		if (records.size() != 1) {
			return false;
		}
		SecretEntity record = records.get(0);
		record.setSecret(secret.toLowerCase());
		secretRepository.save(record);
		return true;
	}
	
	public StatusMessage saveSecrets(List<AuthSecret> secrets) {
		String adminSecret = "";
		String playerSecret = "";
		for (AuthSecret secret:secrets) {
			if ("admin".equalsIgnoreCase(secret.getSecretType())) {
				adminSecret = secret.getSecret();				
			}
			if ("player".equalsIgnoreCase(secret.getSecretType())) {
				playerSecret = secret.getSecret();				
			}
		}
		if (adminSecret.isEmpty()||playerSecret.isEmpty()) {
			return new StatusMessage(1,"No existing secrets found");
		}
		if (! updateSecret("admin", adminSecret))	 {
			return new StatusMessage(1,"Could not update admin pwd");			
		}
		if (! updateSecret("player", playerSecret))	 {
			return new StatusMessage(1,"Could not update player pwd");			
		}
		return new StatusMessage(0,"Secrets updated");	
	}
}
