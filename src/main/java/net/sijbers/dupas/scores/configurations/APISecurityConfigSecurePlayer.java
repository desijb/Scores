package net.sijbers.dupas.scores.configurations;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.SecurityFilterChain;

import lombok.extern.slf4j.Slf4j;
import net.sijbers.dupas.scores.model.db.SecretEntity;
import net.sijbers.dupas.scores.repositories.ISecretRepository;
import net.sijbers.dupas.scores.security.APIKeyAuthFilter;

@Slf4j

@Configuration
@EnableWebSecurity
@Order(1)
public class APISecurityConfigSecurePlayer {
    @Value("${dupas.auth-token-header}")
    private String principalRequestHeader;

    private String principalRequestPlayerValue;      
    private String principalRequestAdminValue;
    
	@Autowired
	ISecretRepository secretRepository;

    @Bean
    public SecurityFilterChain filterPlayerChainSecure(HttpSecurity httpSecurity) throws Exception {
    	List<SecretEntity> secrets = secretRepository.findAll();
    	for (SecretEntity secret:secrets) {
    		if (secret.getSecretType().equalsIgnoreCase("player")) {
    			principalRequestPlayerValue = secret.getSecret();
    			log.info("player secret: {}", principalRequestPlayerValue);
    		}
    		if (secret.getSecretType().equalsIgnoreCase("admin")) {
    			principalRequestAdminValue = secret.getSecret();
    			log.info("admin secret: {}", principalRequestAdminValue);
    		}
    	}
    	if ((principalRequestAdminValue == null)||(principalRequestAdminValue.isEmpty())) {
    		principalRequestAdminValue = "admin";
    	}
    	if ((principalRequestPlayerValue == null)||(principalRequestPlayerValue.isEmpty())) {
    		principalRequestPlayerValue = "player";
    	}

        APIKeyAuthFilter playerFilter = new APIKeyAuthFilter(principalRequestHeader);
        playerFilter.setAuthenticationManager(new AuthenticationManager() {
        	
            @Override
            public Authentication authenticate(Authentication authentication) throws AuthenticationException {
                String principal = (String) authentication.getPrincipal();
            	log.debug("player: {}", principal);
                if ((!principalRequestAdminValue.equals(principal))&&(!principalRequestPlayerValue.equals(principal)))
                {
                	log.debug("player: {} failed", principal);
                    throw new BadCredentialsException("The API key was not found or not the expected value.");
                }
            	log.debug("player: {} succes", principal);
                authentication.setAuthenticated(true);
                return authentication;
            }
        });
        
        httpSecurity
        	.csrf(csrf -> csrf.disable())
        	.securityMatcher("/api/player/**")
        	.sessionManagement(manager -> manager.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .addFilter(playerFilter)
            .authorizeHttpRequests(
            		(request) ->
            		request
            			.requestMatchers("/api/player/**").authenticated()
            		);
        
    	return httpSecurity.build();
    	

    }
}
