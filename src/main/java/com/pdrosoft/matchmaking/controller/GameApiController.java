package com.pdrosoft.matchmaking.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pdrosoft.matchmaking.dto.GameDTO;
import com.pdrosoft.matchmaking.security.payload.MatchmakingUserDetails;
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

	@PutMapping(path = "/game", produces = { "application/json" })
	public GameDTO addGame(@AuthenticationPrincipal MatchmakingUserDetails userDetails) {
		return matchmakingService.addGame(userDetails.getPlayer());
	}
	
	@PostMapping(path = "/game/{gameId:[0-9]+}/join", produces = { "application/json" })
	public GameDTO joinGame(@AuthenticationPrincipal MatchmakingUserDetails userDetails, @PathVariable("gameId") Long gameId) {
		return matchmakingService.joinGame(userDetails.getPlayer(), gameId);
	}

	@PostMapping(path = "/game/{gameId:[0-9]+}/leave", produces = { "application/json" })
	public GameDTO leaveGame(@AuthenticationPrincipal MatchmakingUserDetails userDetails, @PathVariable("gameId") Long gameId) {
		return matchmakingService.leaveGame(userDetails.getPlayer(), gameId);
	}
}
