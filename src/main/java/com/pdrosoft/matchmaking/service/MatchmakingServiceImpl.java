package com.pdrosoft.matchmaking.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pdrosoft.matchmaking.dao.GameDAO;
import com.pdrosoft.matchmaking.dto.GameDTO;

import lombok.NonNull;

@Service
public class MatchmakingServiceImpl implements MatchmakingService {

	@NonNull
	private final GameDAO gameDao;

	public MatchmakingServiceImpl(@Autowired GameDAO gameDao) {
		this.gameDao = gameDao;
	}

	@Override
	public List<GameDTO> getGameList() {
		return gameDao.getGameList();
	}
}
