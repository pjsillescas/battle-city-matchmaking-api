package com.pdrosoft.matchmaking.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pdrosoft.matchmaking.dto.GameDTO;
import com.pdrosoft.matchmaking.service.MatchmakingService;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@RestController
@RequestMapping("/api")
public class GameApiController {

	@NonNull
	private final MatchmakingService matchmakingService;

	public GameApiController(@Autowired MatchmakingService matchmakingService) {
		this.matchmakingService = matchmakingService;
	}

	@Value
	@Builder
	private static class Result {
		public String message;
	}
	
	@GetMapping(path = "/test", produces = { "application/json" })
	public Result testController() {
		return Result.builder().message("hello world").build();
	}
	
	@GetMapping(path = "/game", produces = { "application/json" })
	public List<GameDTO> getGames() {
		return matchmakingService.getGameList();
	}
}
