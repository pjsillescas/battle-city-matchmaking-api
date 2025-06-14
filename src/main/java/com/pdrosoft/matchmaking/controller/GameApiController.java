package com.pdrosoft.matchmaking.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pdrosoft.matchmaking.dto.GameDTO;
import com.pdrosoft.matchmaking.dto.GameExtendedDTO;
import com.pdrosoft.matchmaking.dto.GameInputDTO;
import com.pdrosoft.matchmaking.security.payload.MatchmakingUserDetails;
import com.pdrosoft.matchmaking.service.MatchmakingService;

import jakarta.validation.Valid;
import lombok.NonNull;

@RestController
@RequestMapping("/api")
public class GameApiController {

	@NonNull
	private final MatchmakingService matchmakingService;

	public GameApiController(@Autowired MatchmakingService matchmakingService) {
		this.matchmakingService = matchmakingService;
	}

	@GetMapping(path = "/game", produces = { "application/json" })
	public List<GameDTO> getGames() {
		return matchmakingService.getGameList();
	}

	@PutMapping(path = "/game", produces = { "application/json" })
	public GameDTO addGame(@AuthenticationPrincipal MatchmakingUserDetails userDetails, @Valid @RequestBody GameInputDTO gameInputDto) {
		return matchmakingService.addGame(userDetails.getPlayer(), gameInputDto);
	}
	
	@PostMapping(path = "/game/{gameId:[0-9]+}/join", produces = { "application/json" })
	public GameExtendedDTO joinGame(@AuthenticationPrincipal MatchmakingUserDetails userDetails, @PathVariable("gameId") Long gameId) {
		return matchmakingService.joinGame(userDetails.getPlayer(), gameId);
	}

	@PostMapping(path = "/game/{gameId:[0-9]+}/leave", produces = { "application/json" })
	public GameDTO leaveGame(@AuthenticationPrincipal MatchmakingUserDetails userDetails, @PathVariable("gameId") Long gameId) {
		return matchmakingService.leaveGame(userDetails.getPlayer(), gameId);
	}
}
