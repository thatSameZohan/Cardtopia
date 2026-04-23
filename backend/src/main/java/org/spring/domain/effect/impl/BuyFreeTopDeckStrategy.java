package org.spring.domain.effect.impl;

import org.spring.dto.EffectDto;
import org.spring.dto.GameState;
import org.spring.dto.PlayerState;
import org.spring.domain.effect.EffectType;
import org.spring.domain.effect.EffectStrategy;
import org.springframework.stereotype.Component;

@Component
public class BuyFreeTopDeckStrategy implements EffectStrategy {

    @Override
    public EffectType getType() {
        return EffectType.BUY_FREE_TOP_DECK;
    }

    @Override
    public void applyEffect(EffectDto effect, PlayerState player, GameState gs) {
        player.setBuyFreeTopDeck(player.getBuyFreeTopDeck() + effect.getValue());
    }
}