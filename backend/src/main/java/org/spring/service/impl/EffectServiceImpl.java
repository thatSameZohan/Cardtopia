package org.spring.service.impl;

import org.spring.dto.*;
import org.spring.enums.CardType;
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

    @Override
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
        switch (effect.getType()) {
            case COMBAT -> player.setCurrentAttack(player.getCurrentAttack() + effect.getValue());
            case GOLD -> player.setCurrentGold(player.getCurrentGold() + effect.getValue());
            case DRAW -> draw(player, effect.getValue());
            case HEALTH -> player.setHealth(player.getHealth() + effect.getValue());
            case EXILE -> player.setRightExile(player.getRightExile() + effect.getValue());
            case FORCE_DISCARD -> {
                PlayerState opponent = gs.getPlayers().values().stream()
                        .filter(p -> !p.getPlayerId().equals(player.getPlayerId()))
                        .findFirst()
                        .orElseThrow(() -> new GameCommonException("PLAYER_NOT_FOUND", "Игрок не найден"));

                opponent.setForcedDiscard(opponent.getForcedDiscard() + effect.getValue());
            }
            case DISCARD_DRAW -> {} //TODO реализовать эффект
            case DESTROY_BASE -> player.setDestroyBase(player.getDestroyBase() + effect.getValue());
            case SELECT_FACTION -> {}
            case TOP_DECK_NEXT_SHIP -> player.setTopDeckNextShip(player.getTopDeckNextShip() + effect.getValue());
            case INCREASE_COMBAT -> {
                long count = Stream.of(player.getHand(), player.getPlayedCards())
                        .flatMap(List::stream)
                        .filter(card -> card.getType().equals(CardType.SHIP))
                        .count();

                player.setCurrentAttack(player.getCurrentAttack() + (int) (count * effect.getValue()));
            }
            case DRAW_CONDITIONAL_BASE -> {
                if (player.getBases().size() >= effect.getValue()) {
                    draw(player, effect.getValue());
                }
            }
            case EXILE_DRAW -> {
                player.setRightExile(player.getRightExile() + effect.getValue());
                draw(player, effect.getValue());
            } // TODO доработать эффект
            case BUY_FREE_TOP_DECK -> { player.setBuyFreeTopDeck(player.getBuyFreeTopDeck() + effect.getValue()); }
            case EMPTY -> {
            }
        }
    }

    private boolean hasFactionSupport(CardInstance card, PlayerState player, int required) {
        Faction faction = card.getFaction();

        if (faction == Faction.NEUTRAL) {
            return false;
        }

        long count = Stream.of(
                        player.getHand(),
                        player.getPlayedCards(),
                        player.getBases(),
                        player.getOutposts())
                .flatMap(List::stream)
                .filter(c -> c.getFaction() == faction)
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
