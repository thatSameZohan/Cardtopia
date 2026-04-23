package org.spring.domain.effect.impl;

import org.spring.dto.EffectDto;
import org.spring.dto.GameState;
import org.spring.dto.PlayerState;
import org.spring.domain.effect.EffectStrategy;
import org.spring.domain.effect.EffectType;
import org.springframework.stereotype.Component;

@Component
public class HealthEffectStrategy implements EffectStrategy {

    @Override
    public EffectType getType() {
        return EffectType.HEALTH;
    }

    @Override
    public void applyEffect(EffectDto effect, PlayerState player, GameState gs) {
        player.setHealth(player.getHealth() + effect.getValue());
    }
}