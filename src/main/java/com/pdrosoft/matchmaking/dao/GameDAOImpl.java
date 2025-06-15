package com.pdrosoft.matchmaking.dao;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pdrosoft.matchmaking.dto.GameDTO;
import com.pdrosoft.matchmaking.dto.GameExtendedDTO;
import com.pdrosoft.matchmaking.dto.GameInputDTO;
import com.pdrosoft.matchmaking.dto.PlayerDTO;
import com.pdrosoft.matchmaking.exception.MatchmakingValidationException;
import com.pdrosoft.matchmaking.exception.NotFoundException;
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
		return Optional.ofNullable(player).map(_ -> PlayerDTO.builder() //
				.id(player.getId()) //
				.username(player.getUserName()) //
				.build()).orElse(null);
	}

	private GameDTO toGameDTO(Game game) {
		return Optional.ofNullable(game).map(_ -> GameDTO.builder() //
				.id(game.getId()) //
				.creationDate(game.getCreationDate()) //
				.name(game.getName()) //
				.host(toPlayerDTO(game.getHost())) //
				.guest(toPlayerDTO(game.getGuest())) //
				.build()).orElse(null);
	}

	private GameExtendedDTO toGameExtendedDTO(Game game) {
		return Optional.ofNullable(game).map(_ -> GameExtendedDTO.builder() //
				.id(game.getId()) //
				.creationDate(game.getCreationDate()) //
				.name(game.getName()) //
				.joinCode(game.getJoinCode()) //
				.host(toPlayerDTO(game.getHost())) //
				.guest(toPlayerDTO(game.getGuest())) //
				.build()).orElse(null);
	}

	@Override
	@Transactional(readOnly = true)
	public List<GameDTO> getGameList(Instant dateFrom) {
		var cb = em.getCriteriaBuilder();
		var cq = cb.createQuery(Game.class);
		var root = cq.from(Game.class);

		System.out.println("dateFrom '%s'".formatted(dateFrom));
		// cq.select(root).where(cb.isTrue(cb.literal(true)));
		cq.select(root).where(cb.and(//
				cb.isNull(root.get("guest")), //
				cb.greaterThan(root.get("creationDate"), dateFrom) //
		)).orderBy(cb.desc(root.get("creationDate")));

		return em.createQuery(cq).getResultStream().map(this::toGameDTO).toList();
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public GameDTO addGame(Player host, GameInputDTO gameInputDto) {
		var game = new Game();
		game.setCreationDate(Instant.now());
		game.setName("%s's game".formatted(host.getUserName()));
		game.setJoinCode(gameInputDto.getJoinCode());
		game.setHost(host);

		return Optional.of(gameRepository.save(game)).map(this::toGameDTO).orElseThrow();
	}

	private Optional<Game> loadGame(Long gameId) {
		return gameRepository.findById(gameId);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public GameExtendedDTO joinGame(Player guest, Long gameId) {
		var game = loadGame(gameId)
				.orElseThrow(() -> new NotFoundException("Game %d does not exist".formatted(gameId)));
		if (game.getHost().equals(guest)) {
			throw new MatchmakingValidationException("In a game, the host and the guest cannot be the same user");
		}

		game.setGuest(guest);

		return Optional.ofNullable(gameRepository.save(game)).map(this::toGameExtendedDTO) //
				.orElseThrow(() -> new MatchmakingValidationException("Error saving game"));
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public GameDTO leaveGame(Player player, Long gameId) {
		return loadGame(gameId).map(game -> {

			if (player.equals(game.getHost())) {
				gameRepository.delete(game);
				return null;
			}

			if (player.equals(game.getGuest())) {
				game.setGuest(null);
				return Optional.ofNullable(gameRepository.save(game)).map(this::toGameDTO) //
						.orElseThrow(() -> new MatchmakingValidationException("Error saving game"));

			}

			throw new MatchmakingValidationException(
					"Player %d is not a member of the game %s".formatted(player.getUserName(), game.getName()));
		}).orElse(null);
	}
}
