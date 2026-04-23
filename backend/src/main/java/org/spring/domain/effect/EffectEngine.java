package org.spring.domain.effect;

import org.spring.dto.*;
import org.spring.domain.card.CardFaction;
import org.spring.exc.GameCommonException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Service
public class EffectEngine {

    private final Map<EffectType, EffectStrategy> strategies;

    public EffectEngine(List<EffectStrategy> strategies) {
        this.strategies = strategies.stream()
                .collect(Collectors.toMap(
                        EffectStrategy::getType,
                        Function.identity()));
    }

    public void applyPlayEffects(CardInstance card, PlayerState player, GameState gs) {

        // 1. всегда применяем main effects
        applySet(card.getMainEffects(), player, gs);
        // 2. проверяем фракционное условие 1 уровня
        if (hasFactionSupport(card, player,1)) {
            applySet(card.getFactionEffectsLvl1(), player, gs);
        }
        // 2. проверяем фракционное условие 2 уровня
        if (hasFactionSupport(card, player,2)) {
            // TODO реализовать эффекты фракции 2 уровня
        }
    }

    public void applyScrapEffects(CardInstance card, PlayerState player, GameState gs) {
        applySet(card.getScrapEffects(), player, gs);
    }

    private void applySet(SetEffects set, PlayerState player, GameState gs) {
        if (set == null || set.getEffects().isEmpty()) {
           return;
        }
        switch (set.getType()) {
            case AND -> set.getEffects().forEach(e -> applyEffect(e, player, gs));
            case OR -> applyEffect(set.getEffects().getFirst(), player,gs); // временно выбираем первый эффект
        }
    }

    private void applyEffect(EffectDto effect, PlayerState player, GameState gs) {
        EffectStrategy strategy = strategies.get(effect.getType());

        if (strategy == null) {
            throw new IllegalArgumentException("Неизвестный эффект: " + effect.getType());
        }

        strategy.applyEffect(effect, player, gs);
    }

    private boolean hasFactionSupport(CardInstance card, PlayerState player, int required) {
        CardFaction cardFaction = card.getCardFaction();

        if (cardFaction == CardFaction.NEUTRAL) {
            return false;
        }

        long count = Stream.of(
                        player.getHand(),
                        player.getPlayedCards(),
                        player.getBases(),
                        player.getOutposts())
                .flatMap(List::stream)
                .filter(c -> c.getCardFaction() == cardFaction)
                .filter(c -> c != card)
                .count();

        return count >= required;
    }
}
