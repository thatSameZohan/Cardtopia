package org.spring.dto;

public record DestroyBaseRequest(
        String gameId,
        String BaseId
) {}