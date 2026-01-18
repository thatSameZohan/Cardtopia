package org.spring.service;

import org.spring.dto.CardInstance;
import org.spring.dto.GameState;
import org.spring.dto.PlayerState;

public interface EffectService {

    void applyPlayEffects(CardInstance card, PlayerState player, GameState gs);

    void applyScrapEffects(CardInstance card, PlayerState player, GameState gs);
}
