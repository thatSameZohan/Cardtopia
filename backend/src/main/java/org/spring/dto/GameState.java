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

    private final String roomId;
    private GameStatus status;
    private final Map<String, PlayerState> players = new LinkedHashMap<>();
    private String activePlayerId;
    private final List<Card> marketDeck = new LinkedList<>();
    private final List<Card> market = new ArrayList<>(); // 5 visible cards

    public GameState(String roomId) {
        this.roomId = roomId;
        this.status = GameStatus.WAITING_FOR_PLAYER;
    }

    public Collection<String> getPlayerIds() {
        return players.keySet();
    }
}
