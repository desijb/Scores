package net.sijbers.dupas.scores.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import net.sijbers.dupas.scores.model.AuthSecret;
import net.sijbers.dupas.scores.model.StatusMessage;
import net.sijbers.dupas.scores.services.SecretService;

@Slf4j
@RestController
@RequestMapping("/api/admin/secret")
@Tag(name = "Secret Admin API", description = "Secrets Calls (admin only)")
public class AdminSecretController {
	
	@Autowired
	SecretService secretService;
	
	@RequestMapping(value = "/all", method = RequestMethod.GET)
	@Operation(summary = "Get All secrets")
	public List<AuthSecret> getAllSeasons() {	
		log.debug("get all secrets");
		return secretService.getAllSecrets();
	}
	
	@RequestMapping(value = "/save", method = RequestMethod.POST)
	@Operation(summary = "Save Secrets")
	public StatusMessage saveSecrets(@RequestBody List<AuthSecret> secrets) {		
		return secretService.saveSecrets(secrets);
	}

}
