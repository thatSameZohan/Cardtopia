package org.spring.domain.effect.impl;

import org.spring.domain.effect.EffectStrategy;
import org.spring.domain.effect.EffectType;
import org.spring.dto.EffectDto;
import org.spring.dto.GameState;
import org.spring.dto.PlayerState;
import org.springframework.stereotype.Component;

@Component
public class DiscardDrawStrategy implements EffectStrategy {

    @Override
    public EffectType getType() {
        return EffectType.DISCARD_DRAW;
    }

    @Override
    public void applyEffect(EffectDto effect, PlayerState player, GameState gameState) {
        // TODO реализовать эффект
    }
}
