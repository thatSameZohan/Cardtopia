package org.spring.service.impl;

import org.spring.dto.AbilityDto;
import org.spring.dto.CardDto;
import org.spring.dto.GameState;
import org.spring.dto.PlayerState;
import org.spring.enums.AbilityType;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.LinkedList;

@Service
public class AbilityService {

    /**
     * Применяет все способности карты по её триггеру
     */
    public void applyAbilities(CardDto card, PlayerState player, GameState gs, String trigger) {

        if (card.getAbilities() == null) {
            return;
        }

        for (AbilityDto ability : card.getAbilities()) {

            applyAbility(ability,player,gs);
//            if (trigger.equalsIgnoreCase(ability.getTrigger()) && checkCondition(ability.getCondition(), player, gs)) {
//                applyAbility(ability, player, gs);
//            }
        }
    }

    /**
     * Суммарное значение атаки карты с учётом способностей
     */
    public int getAttackValue(CardDto card, PlayerState player, GameState gs) {
        if (card.getAbilities() == null) return 0;

        return card.getAbilities().stream()
                .filter(a -> a.getType() == AbilityType.COMBAT)
//                .filter(a -> checkCondition(a.getCondition(), player, gs))
                .mapToInt(AbilityDto::getValue)
                .sum();
    }

    private void applyAbility(AbilityDto ability, PlayerState player, GameState gs) {
        switch (ability.getType()) {
            case COMBAT -> player.setCurrentAttack(player.getCurrentAttack() + ability.getValue());
            case TRADE -> player.setCurrentGold(player.getCurrentGold() + ability.getValue());
            case DRAW -> drawCards(player, ability.getValue());
            case SCRAP -> scrapCards(player, gs, ability.getValue());
//            case HEAL -> player.setHealth(player.getHealth() + ability.getValue());
//            case ALLY -> triggerAllyAbility(player, gs, ability);
        }
    }

    private void drawCards(PlayerState player, int count) {
        for (int i = 0; i < count; i++) {
            if (player.getDeck().isEmpty()) {
                Collections.shuffle(player.getDiscardPile());
                player.setDeck(new LinkedList<>(player.getDiscardPile()));
                player.getDiscardPile().clear();
            }
            if (player.getDeck().isEmpty()) break;
            player.getHand().add(player.getDeck().removeFirst());
        }
    }

    private void scrapCards(PlayerState player, GameState gs, int count) {
        for (int i = 0; i < count && !player.getHand().isEmpty(); i++) {
            player.getHand().removeFirst();
        }
    }

    private void triggerAllyAbility(PlayerState player, GameState gs, AbilityDto ability) {
        // Пример: увеличить золото, если есть карта определенного типа в play
        player.setCurrentGold(player.getCurrentGold() + ability.getValue());
    }

    /**
     * Проверка условия применения способности
     * Пока базовая реализация: если condition пустое или null -> true
     * Можно позже расширять: парсить строку и проверять карты в игре, типы, хп и т.д.
     */
    private boolean checkCondition(String condition, PlayerState player, GameState gs) {
        if (condition == null || condition.isBlank()) return true;

        // Простейшие примеры условий:
        if (condition.equalsIgnoreCase("if hand empty")) return player.getHand().isEmpty();
        if (condition.equalsIgnoreCase("if opponent alive")) {
            String opponentId = gs.getPlayers().keySet().stream()
                    .filter(id -> !id.equals(player.getPlayerId()))
                    .findFirst().orElse(null);
            return opponentId != null && gs.getPlayers().get(opponentId).getHealth() > 0;
        }

        // TODO: добавить разбор сложных условий
        return true;
    }
}
