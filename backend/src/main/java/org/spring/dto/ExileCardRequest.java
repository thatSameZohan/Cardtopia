package org.spring.dto;

public record ExileCardRequest(
        String gameId,
        String cardId,
        String cardCode
) {}