package org.spring.dto;

import java.util.List;

public record PrivatePlayerView(
        List<CardDto> hand,
        List<CardDto> playedCard
) {}