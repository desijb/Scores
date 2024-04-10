package net.sijbers.dupas.scores;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

@SpringBootApplication
public class ScoresApplication {
		
    @Bean
    public OpenAPI customOpenAPI(
    		@Value("${application-description}") 
    		String appDesciption, 
    		@Value("${application-version}") 
    		String appVersion,
    		@Value("${spring.application.name}") 
    		String appName) {
     return new OpenAPI()
	    		.addSecurityItem(new SecurityRequirement().addList("apiKeyScheme"))
	 			.components(new Components().addSecuritySchemes("apiKeyScheme", new SecurityScheme()
	 					.type(SecurityScheme.Type.APIKEY)
	 					.in(SecurityScheme.In.HEADER)
	 					.name("dupas-auth-header")
	 			))    		 
          .info(new Info()
          .title(appName)
          .version(appVersion)
          .description(appDesciption)
          .termsOfService("http://sijbers.net")
          .license(new License().name("SijberSpace").url("http://sijbers.net")));
    }
    
	public static void main(String[] args) {
		 SpringApplication.run(ScoresApplication.class, args);
	}	
}
