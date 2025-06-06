package com.pdrosoft.matchmaking.dao;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pdrosoft.matchmaking.dto.GameDTO;
import com.pdrosoft.matchmaking.dto.PlayerDTO;
import com.pdrosoft.matchmaking.model.Game;
import com.pdrosoft.matchmaking.model.Player;
import com.pdrosoft.matchmaking.repository.GameRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.NonNull;

@Service
public class GameDAOImpl implements GameDAO {
	@PersistenceContext
	private EntityManager em;

	@NonNull
	private final GameRepository gameRepository;
	
	public GameDAOImpl(@Autowired GameRepository gameRepository) {
		this.gameRepository = gameRepository;
	}

	@Override
	public void createGameWithCreator(Player host, String gameName) {
		var game = new Game();
		game.setName(gameName);
		game.setHost(host);
		game.setCreationDate(Instant.now());

		gameRepository.save(game); // Cascade saves everything
	}

	private PlayerDTO toPlayerDTO(Player player) {
		return Optional.ofNullable(player).map(p -> PlayerDTO.builder() //
				.id(player.getId()) //
				.username(player.getUsername()) //
				.build()).orElse(null);
	}

	private GameDTO toGameDTO(Game game) {
		return Optional.ofNullable(game).map(g -> GameDTO.builder() //
				.id(game.getId()) //
				.creationDate(game.getCreationDate()) //
				.host(toPlayerDTO(game.getHost())) //
				.guest(toPlayerDTO(game.getGuest())) //
				.build()).orElse(null);
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<GameDTO> getGameList() {
		var cb = em.getCriteriaBuilder();
		var cq = cb.createQuery(Game.class);
		var root = cq.from(Game.class);
		cq.select(root).where(cb.isTrue(cb.literal(true)));

		return em.createQuery(cq).getResultStream().map(this::toGameDTO).toList();
	}
}
