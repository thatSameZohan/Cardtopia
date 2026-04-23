package org.spring.dto;

import org.spring.domain.card.CardCode;

public record ExileCardRequest(
        String gameId,
        String cardId,
        CardCode cardCode
) {}