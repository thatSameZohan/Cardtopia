package org.spring.domain.effect.impl;

import org.spring.dto.EffectDto;
import org.spring.dto.GameState;
import org.spring.dto.PlayerState;
import org.spring.domain.effect.EffectStrategy;
import org.spring.domain.effect.EffectType;
import org.springframework.stereotype.Component;

@Component
public class ForceDiscardEffectStrategy implements EffectStrategy {

    @Override
    public EffectType getType() {
        return EffectType.FORCE_DISCARD;
    }

    @Override
    public void applyEffect(EffectDto effect, PlayerState player, GameState gs) {

        PlayerState opponent = gs.getOpponent(player.getPlayerId());

        opponent.setForcedDiscard(opponent.getForcedDiscard() + effect.getValue());
    }
}