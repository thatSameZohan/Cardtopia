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
    private final Map<String, PlayerState> players = new LinkedHashMap<>();
    private GameStatus status;
    private String activePlayerId;
    private final Deque<CardDto> marketDeck = new ArrayDeque<>();
    private final List<CardDto> market = new ArrayList<>(5);
    private String winnerId;
    private Deque<CardDto> explorerPile = new ArrayDeque<>();

    public GameState(String id) {
        this.id = id;
        this.status = GameStatus.IN_PROGRESS;
    }

    public boolean isPlayersTurn(String playerId) {
        return playerId.equals(activePlayerId);
    }
}

