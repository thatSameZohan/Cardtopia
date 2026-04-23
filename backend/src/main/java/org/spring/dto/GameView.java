package org.spring.dto;

import org.spring.domain.game.GameStatus;
import java.util.List;

public record GameView (
        String gameId,
        String activePlayerId,
        GameStatus status,
        List<PlayerView> players,
        List<CardInstance> market,
        Integer marketDeckSize,
        String winnerId,
        List<CardInstance> explorerPile
) {}