package com.pdrosoft.matchmaking.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pdrosoft.matchmaking.dao.GameDAO;
import com.pdrosoft.matchmaking.dao.PlayerDAO;
import com.pdrosoft.matchmaking.dto.GameDTO;
import com.pdrosoft.matchmaking.dto.PlayerDTO;
import com.pdrosoft.matchmaking.exception.PlayerExistsException;
import com.pdrosoft.matchmaking.model.Player;
import com.pdrosoft.matchmaking.repository.PlayerRepository;

import lombok.NonNull;

@Service
public class MatchmakingServiceImpl implements MatchmakingService {

	@NonNull
	private final GameDAO gameDao;
	@NonNull
	private final PlayerRepository playerRepository;
	@NonNull
	private final PlayerDAO playerDao;

	public MatchmakingServiceImpl(@Autowired GameDAO gameDao, @Autowired PlayerRepository playerRepository,
			PlayerDAO playerDao) {
		this.gameDao = gameDao;
		this.playerRepository = playerRepository;
		this.playerDao = playerDao;
	}

	@Override
	@Transactional(readOnly = true)
	public List<GameDTO> getGameList() {
		return gameDao.getGameList();
	}

	private PlayerDTO toPlayerDTO(Player player) {
		return PlayerDTO.builder().id(player.getId()).username(player.getUserName()).build();

	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public PlayerDTO addPlayer(String name, String password) {
		var playerOpt = playerDao.findPlayersByName(name);

		if (playerOpt.isPresent()) {
			throw new PlayerExistsException("player already exists '%s'".formatted(name));
		}

		var player = new Player();
		player.setUserName(name);
		player.setPassword(password);

		return Optional.ofNullable(playerRepository.save(player)).map(this::toPlayerDTO).orElseThrow();
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public GameDTO addGame(Player host) {
		return gameDao.addGame(host);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public GameDTO joinGame(Player guest, Long gameId) {
		return gameDao.joinGame(guest, gameId);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public GameDTO leaveGame(Player player, Long gameId) {
		return gameDao.leaveGame(player, gameId);
	}

}
