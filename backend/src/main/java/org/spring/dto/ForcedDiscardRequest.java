package org.spring.dto;

public record ForcedDiscardRequest(
        String gameId,
        String cardId
) {}
