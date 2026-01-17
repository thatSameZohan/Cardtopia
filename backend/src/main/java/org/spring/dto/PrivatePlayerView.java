package org.spring.dto;

import java.util.List;

public record PrivatePlayerView(List<CardInstance> hand, List<CardInstance> playedCards, List<CardInstance> discardPile) {}