package org.spring.domain.effect.impl;

import lombok.RequiredArgsConstructor;
import org.spring.domain.deck.DeckService;
import org.spring.dto.EffectDto;
import org.spring.dto.GameState;
import org.spring.dto.PlayerState;
import org.spring.domain.effect.EffectStrategy;
import org.spring.domain.effect.EffectType;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DrawEffectStrategy implements EffectStrategy {

    private final DeckService deckService;

    @Override
    public EffectType getType() {
        return EffectType.DRAW;
    }

    @Override
    public void applyEffect(EffectDto effect, PlayerState player, GameState gameState) {
        deckService.draw(player, effect.getValue());
    }
}
