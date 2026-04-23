package org.spring.domain.effect.impl;

import org.spring.dto.EffectDto;
import org.spring.dto.GameState;
import org.spring.dto.PlayerState;
import org.spring.domain.effect.EffectType;
import org.spring.domain.effect.EffectStrategy;
import org.springframework.stereotype.Component;

@Component
public class TopDeckNextShipStrategy implements EffectStrategy {

    @Override
    public EffectType getType() {
        return EffectType.TOP_DECK_NEXT_SHIP;
    }

    @Override
    public void applyEffect(EffectDto effect, PlayerState player, GameState gs) {
        player.setTopDeckNextShip(player.getTopDeckNextShip() + effect.getValue());
    }
}