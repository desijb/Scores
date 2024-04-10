package net.sijbers.dupas.scores.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import net.sijbers.dupas.scores.model.StatusMessage;
import net.sijbers.dupas.scores.services.AdminService;

@Slf4j
@RestController
@RequestMapping("/api/admin")
@Tag(name = "Admin API", description = "Calls for administrators ")
public class AdminController {
	
	@Autowired	
	AdminService adminService;
	
	@RequestMapping(value = "/role", method = RequestMethod.GET)
	@Operation(summary = "check role")
	public StatusMessage  checkRole(HttpServletRequest httpServletRequest) {		
		log.debug("check role");
		
		/*
		log.info("httpServletRequest.getAuthType() : {}",httpServletRequest.getAuthType());
		log.info("httpServletRequest.getContextPath() : {}",httpServletRequest.getContextPath());
		log.info("httpServletRequest.getCharacterEncoding() : {}",httpServletRequest.getCharacterEncoding());
	

		log.info("httpServletRequest.getContentType() : {}",httpServletRequest.getContentType());
		
		Cookie[] cookies = httpServletRequest.getCookies();
		for  (Cookie cookie:cookies) {
			log.info("cookie.getName() : {}",cookie.getName());
			
		}
		
		log.info("httpServletRequest.getLocalAddr() : {}",httpServletRequest.getLocalAddr());
		
		
		log.info("httpServletRequest.getMethod() : {}",httpServletRequest.getMethod());

		log.info("httpServletRequest.getPathInfo() : {}",httpServletRequest.getPathInfo());

		
		log.info("httpServletRequest.getRemoteHost(): {}",httpServletRequest.getRemoteHost());
		log.info("httpServletRequest.getRemoteAddr(): {}",httpServletRequest.getRemoteAddr());
		
		log.info("httpServletRequest.getRemoteUser(): {}",httpServletRequest.getRemoteUser());
		log.info("httpServletRequest.getRequestURI(): {}",httpServletRequest.getRequestURI());
		
		log.info("httpServletRequest.getUserPrincipal().getName(): {}",httpServletRequest.getUserPrincipal().getName());
			
		
		Enumeration<String> headerNames =httpServletRequest.getHeaderNames();
		while (headerNames.hasMoreElements()) {
			String header = headerNames.nextElement();
			log.info("headerNames {}: {}", header, httpServletRequest.getHeader(header));
		}
		HttpSession session = httpServletRequest.getSession();
		Enumeration<String> sessionAttributes = session.getAttributeNames();
		while (sessionAttributes.hasMoreElements()) {
			String attribute = sessionAttributes.nextElement();
			log.info("sessionNames {}: {}", attribute,httpServletRequest.getAttribute(attribute));
		}
		*/
		return new StatusMessage(0,"admin");
	}
}
