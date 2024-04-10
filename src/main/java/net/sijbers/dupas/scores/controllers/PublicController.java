package net.sijbers.dupas.scores.controllers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import net.sijbers.dupas.scores.model.ApplicationVersion;

@RestController
@RequestMapping("/api/public")
@Tag(name = "Public API", description = "Calls not protected by authentication ")

public class PublicController {
	
	@Value("${application-version}")
	private String applicationVersion;

	@RequestMapping(value = "/version", method = RequestMethod.GET)
	@Operation(summary = "Get backend version")
	public ApplicationVersion getVersion() {	
		return new ApplicationVersion(applicationVersion);
	}
}
