package fr.insee.eno.ws.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HomeController {
	
	
	@RequestMapping("/")
	public String redirectToSwaggerUI() {
		return "redirect:/swagger-ui.html";
	}
}
