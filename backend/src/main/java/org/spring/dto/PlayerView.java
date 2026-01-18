package org.spring.dto;
import java.util.List;

public record PlayerView(
        String playerId,
        int currentAttack,
        int currentGold,
        int health,
        int deckSize,
        int discardSize,
        int handSize,
        List<CardInstance> playedCards,
        List<CardInstance> bases,
        List<CardInstance> outposts,
        boolean active
) {}