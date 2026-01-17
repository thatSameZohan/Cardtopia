package org.spring.dto;

import org.spring.enums.GameStatus;

import java.util.List;

public record GameView (
        String gameId,
        String activePlayerId,
        GameStatus status,
        List<PlayerView> players,
        List<CardInstance> market,
        String winnerId
) {}