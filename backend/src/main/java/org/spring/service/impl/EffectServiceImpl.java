package org.spring.service.impl;

import org.spring.dto.*;
import org.spring.enums.Faction;
import org.spring.exc.GameCommonException;
import org.spring.service.EffectService;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;


@Service
public class EffectServiceImpl implements EffectService {

    @Override
    public void applyPlayEffects(CardInstance card, PlayerState player, GameState gs) {

        // 1. всегда применяем main effects
        applySet(card.getDefinition().getMainEffects(), player);
        // 2. проверяем фракционное условие 1 уровня
        if (hasFactionSupport(card, player,1)) {
            applySet(card.getDefinition().getFactionEffectsLvl1(), player);
        }
    }

    @Override
    public void applyScrapEffects(CardInstance card, PlayerState player, GameState gs) {
        applySet(card.getDefinition().getScrapEffects(), player);
    }

    private void applySet(SetEffects set, PlayerState player) {
        if (set == null || set.getEffects().isEmpty()) {
           return;
        }
        switch (set.getType()) {
            case AND -> set.getEffects().forEach(e -> applyEffect(e, player));
            case OR, CAN -> applyEffect(set.getEffects().getFirst(), player); // временно выбираем первый эффект
        }
    }

    private void applyEffect(EffectDto effect, PlayerState player) {
        switch (effect.getType()) {
            case COMBAT -> player.setCurrentAttack(
                    player.getCurrentAttack() + effect.getValue());
            case GOLD -> player.setCurrentGold(
                    player.getCurrentGold() + effect.getValue());
            case DRAW -> draw(player, effect.getValue());
            case HEALTH -> player.setHealth(
                    player.getHealth() + effect.getValue());
            case DESTROY -> {
                // будет реализовано позже
            }
        }
    }

    private boolean hasFactionSupport(CardInstance card, PlayerState player, int required) {
        Faction faction = card.getDefinition().getFaction();

        if (faction == Faction.NEUTRAL) {
            return false;
        }

        long count = Stream.of(
                        player.getHand(),
                        player.getPlayedCards(),
                        player.getBases(),
                        player.getOutposts())
                .flatMap(List::stream)
                .filter(c -> c.getDefinition().getFaction() == faction)
                .filter(c -> c != card)
                .count();

        return count >= required;
    }

    private void draw(PlayerState player, int count) {
        for (int i = 0; i < count; i++) {
            if (player.getDeck().isEmpty()) {
                Collections.shuffle(player.getDiscardPile());
                player.getDeck().addAll(player.getDiscardPile());
                player.getDiscardPile().clear();
            }
            if (player.getDeck().isEmpty()) return;
            player.getHand().add(player.getDeck().removeFirst());
        }
    }
}
