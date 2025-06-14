package com.pdrosoft.matchmaking.dao;

import java.util.List;

import com.pdrosoft.matchmaking.dto.GameDTO;
import com.pdrosoft.matchmaking.dto.GameExtendedDTO;
import com.pdrosoft.matchmaking.dto.GameInputDTO;
import com.pdrosoft.matchmaking.model.Player;

public interface GameDAO {
	void createGameWithCreator(Player host, String gameName);

	List<GameDTO> getGameList();

	GameDTO addGame(Player host, GameInputDTO gameInputDto);

	GameExtendedDTO joinGame(Player guest, Long gameId);

	GameDTO leaveGame(Player leavingPlayer, Long gameId);
}
