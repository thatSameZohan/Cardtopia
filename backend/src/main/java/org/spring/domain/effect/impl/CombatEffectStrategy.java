package org.spring.domain.effect.impl;

import org.spring.dto.EffectDto;
import org.spring.dto.GameState;
import org.spring.dto.PlayerState;
import org.spring.domain.effect.EffectType;
import org.spring.domain.effect.EffectStrategy;
import org.springframework.stereotype.Component;

@Component
public class CombatEffectStrategy implements EffectStrategy {

    @Override
    public EffectType getType() {
        return EffectType.COMBAT;
    }

    @Override
    public void applyEffect(EffectDto effect, PlayerState player, GameState gameState) {
        player.setCurrentAttack(player.getCurrentAttack() + effect.getValue());
    }
}
