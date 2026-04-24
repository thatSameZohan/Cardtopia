package org.spring.dto;

import java.util.List;

public record PrivatePlayerView(String playerId,
                                List<CardInstance> hand,
                                List<CardInstance> discardPile
) {}