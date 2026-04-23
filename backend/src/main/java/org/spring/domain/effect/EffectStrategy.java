package org.spring.domain.effect;

import org.spring.dto.EffectDto;
import org.spring.dto.GameState;
import org.spring.dto.PlayerState;

public interface EffectStrategy {
    EffectType getType();
    void applyEffect(EffectDto effect, PlayerState player, GameState gameState);
}
