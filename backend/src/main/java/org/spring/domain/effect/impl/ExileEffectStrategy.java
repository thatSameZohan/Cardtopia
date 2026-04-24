package org.spring.domain.effect.impl;

import lombok.RequiredArgsConstructor;
import org.spring.domain.effect.EffectStrategy;
import org.spring.domain.effect.EffectType;
import org.spring.dto.EffectDto;
import org.spring.dto.GameState;
import org.spring.dto.PlayerState;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ExileEffectStrategy implements EffectStrategy {

    @Override
    public EffectType getType() {
        return EffectType.EXILE;
    }

    @Override
    public void applyEffect(EffectDto effect, PlayerState player, GameState gs) {
        player.setRightExile(player.getRightExile() + effect.getValue());
    }
}