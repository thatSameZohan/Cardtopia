package org.spring.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.spring.enums.GameStatus;

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

    public GameState(String id) {
        this.id = id;
        this.status = GameStatus.WAITING_FOR_PLAYER;
    }

    public boolean isPlayersTurn(String playerId) {
        return playerId.equals(activePlayerId);
    }
}

