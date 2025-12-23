package org.spring.dto;

import java.util.List;

public record PrivatePlayerView(
        List<Card> hand,
        List<Card> playedCards
) {}