package org.spring.dto;

import org.spring.domain.game.TargetType;

public record AttackRequest(
        String gameId,
        TargetType targetType, // OUTPOST | BASE | PLAYER
        String targetId    // null если PLAYER
) {}
