package org.spring.domain.effect.impl;

import lombok.RequiredArgsConstructor;
import org.spring.domain.deck.DeckService;
import org.spring.dto.EffectDto;
import org.spring.dto.GameState;
import org.spring.dto.PlayerState;
import org.spring.domain.effect.EffectType;
import org.spring.domain.effect.EffectStrategy;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class DrawConditionalBaseStrategy implements EffectStrategy {

    private final DeckService deckService;

    @Override
    public EffectType getType() {
        return EffectType.DRAW_CONDITIONAL_BASE;
    }

    @Override
    public void applyEffect(EffectDto effect, PlayerState player, GameState gs) {

        if (player.getBases().size() >= effect.getValue()) {
            deckService.draw(player, effect.getValue());
        }
    }
}