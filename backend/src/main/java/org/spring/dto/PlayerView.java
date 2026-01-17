package org.spring.dto;

public record PlayerView(
        String playerId,
        int health,
        int handSize,
        int playedSize,
        int discardSize,
        int deckSize,
        int currentAttack,
        int currentGold,
        boolean active
) {}