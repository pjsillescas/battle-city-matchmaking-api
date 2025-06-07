package com.pdrosoft.matchmaking.service;

import java.util.List;

import com.pdrosoft.matchmaking.dto.GameDTO;
import com.pdrosoft.matchmaking.dto.PlayerDTO;
import com.pdrosoft.matchmaking.model.Player;

public interface MatchmakingService {

	List<GameDTO> getGameList();

	PlayerDTO addPlayer(String name, String password);

	GameDTO addGame(Player host);

	GameDTO joinGame(Player guest, Long gameId);

	GameDTO leaveGame(Player player, Long gameId);

}
