package org.spring.dto;

import java.util.List;

public record PlayerPrivateView(
        List<Card> hand,
        List<Card> playedCards
) {}