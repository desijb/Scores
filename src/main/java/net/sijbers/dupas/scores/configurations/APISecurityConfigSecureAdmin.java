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
@Order(2)
public class APISecurityConfigSecureAdmin {
    @Value("${dupas.auth-token-header}")
    private String principalRequestHeader;
    
    private String principalRequestAdminValue;
    
	@Autowired
	ISecretRepository secretRepository;

    @Bean
    public SecurityFilterChain filterAdminChainSecure(HttpSecurity httpSecurity) throws Exception {
    	
    	List<SecretEntity> secrets = secretRepository.findAll();
    	for (SecretEntity secret:secrets) {
    		if (secret.getSecretType().equalsIgnoreCase("admin")) {
    			principalRequestAdminValue = secret.getSecret();
    			log.info("admin secret: {}", principalRequestAdminValue);
    		}
    	}
    	if ((principalRequestAdminValue == null)||(principalRequestAdminValue.isEmpty())) {
    		principalRequestAdminValue = "admin";
    	}
    	
        APIKeyAuthFilter adminFilter = new APIKeyAuthFilter(principalRequestHeader);
        adminFilter.setAuthenticationManager(new AuthenticationManager() {

            @Override
            public Authentication authenticate(Authentication authentication) throws AuthenticationException {
            	
//            	WebAuthenticationDetails webAuthenticationDetails = (WebAuthenticationDetails)  authentication.getDetails();
//            	log.info("ip: {}",webAuthenticationDetails.getRemoteAddress());
                      		            	
                String principal = (String) authentication.getPrincipal();
            	log.debug("admin: {}", principal);
                if (!principalRequestAdminValue.equals(principal))
                {
                	log.debug("admin: {} failed", principal);
                    throw new BadCredentialsException("The API key was not found or not the expected value.");
                }
            	log.debug("admin: {} success", principal);
                authentication.setAuthenticated(true);
                return authentication;
            }
        });
        
        httpSecurity
    		.csrf(csrf -> csrf.disable())
    		.securityMatcher("/api/admin/**")
        	.sessionManagement(manager -> manager.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
    		.addFilter(adminFilter)
            .authorizeHttpRequests(
            		(request) ->
            		request
            			.requestMatchers("/api/admin/**").authenticated()
            		);
    
        return httpSecurity.build();    	
    }
}
