package org.spring.domain.effect.impl;

import org.spring.dto.EffectDto;
import org.spring.dto.GameState;
import org.spring.dto.PlayerState;
import org.spring.domain.effect.EffectType;
import org.spring.domain.effect.EffectStrategy;
import org.spring.domain.card.CardType;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Stream;

@Component
public class IncreaseCombatEffectStrategy implements EffectStrategy {

    @Override
    public EffectType getType() {
        return EffectType.INCREASE_COMBAT;
    }

    @Override
    public void applyEffect(EffectDto effect, PlayerState player, GameState gs) {

        long count = Stream.of(player.getHand(), player.getPlayedCards())
                .flatMap(List::stream)
                .filter(card -> card.getType() == CardType.SHIP)
                .count();

        player.setCurrentAttack(player.getCurrentAttack() + (int) (count * effect.getValue()));
    }
}