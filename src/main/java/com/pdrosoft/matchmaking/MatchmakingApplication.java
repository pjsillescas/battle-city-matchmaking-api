package com.pdrosoft.matchmaking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {
	    "com.pdrosoft.matchmaking.controller",
	    "com.pdrosoft.matchmaking.service",
	    "com.pdrosoft.matchmaking.dao",
	    "com.pdrosoft.matchmaking.security",
	})
public class MatchmakingApplication {

	public static void main(String[] args) {
		SpringApplication.run(MatchmakingApplication.class, args);
	}

}
