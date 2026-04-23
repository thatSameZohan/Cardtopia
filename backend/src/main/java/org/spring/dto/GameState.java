package org.spring.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.spring.domain.game.GameStatus;
import org.spring.exc.GameCommonException;

import java.util.*;

@Getter
@Setter
@ToString
public class GameState {

    private final String id;
    private Map<String, PlayerState> players = new LinkedHashMap<>();
    private GameStatus status;
    private String activePlayerId;
    private List<CardInstance> marketDeck = new LinkedList<>();
    private List<CardInstance> market = new ArrayList<>(5);
    private String winnerId;
    private List<CardInstance> explorerPile = new ArrayList<>();

    public GameState() {
        this.id = UUID.randomUUID().toString().substring(0, 8);
        this.status = GameStatus.WAITING_FOR_PLAYER;
    }

    public boolean isPlayersTurn(String playerId) {
        return playerId.equals(activePlayerId);
    }

    public PlayerState getPlayer(String playerId) {

        PlayerState player = players.get(playerId);

        if (player == null) {
            throw new GameCommonException("PLAYER_NOT_FOUND", "Игрок не найден");
        }

        return player;
    }

    public PlayerState getOpponent(String playerId) {
        return players.values().stream()
                .filter(p -> !p.getPlayerId().equals(playerId))
                .findFirst()
                .orElseThrow(() ->
                        new GameCommonException("OPPONENT_NOT_FOUND", "Оппонент не найден"));
    }



}

