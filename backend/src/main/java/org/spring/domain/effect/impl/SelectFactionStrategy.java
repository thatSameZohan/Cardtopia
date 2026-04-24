package org.spring.domain.effect.impl;

import org.spring.domain.effect.EffectStrategy;
import org.spring.domain.effect.EffectType;
import org.spring.dto.EffectDto;
import org.spring.dto.GameState;
import org.spring.dto.PlayerState;
import org.springframework.stereotype.Component;

@Component
public class SelectFactionStrategy implements EffectStrategy {

    @Override
    public EffectType getType() {
        return EffectType.SELECT_FACTION;
    }

    @Override
    public void applyEffect(EffectDto effect, PlayerState player, GameState gs) {
        // эффект применяется в GameActionService
        }
}
