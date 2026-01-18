package org.spring.dto;

public record AttackRequest(
        String gameId,
        String targetType, // OUTPOST | BASE | PLAYER
        String targetId    // null если PLAYER
) {}
