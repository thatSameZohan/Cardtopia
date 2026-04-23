package org.spring.domain.market.impl;

import lombok.RequiredArgsConstructor;
import org.spring.domain.market.PurchaseStrategy;
import org.spring.domain.market.PurchaseType;
import org.spring.domain.validators.GameActionValidator;
import org.spring.dto.CardInstance;
import org.spring.dto.GameState;
import org.spring.dto.PlayerState;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class NormalPurchaseStrategy implements PurchaseStrategy {

    private final GameActionValidator gameActionValidator;

    @Override
    public PurchaseType getType() {
        return PurchaseType.NORMAL;
    }

    @Override
    public void buy(CardInstance card, PlayerState player, GameState gs) {

        gameActionValidator.validateGoldForBuy(player, card);

        player.setCurrentGold(player.getCurrentGold() - card.getCost());
        player.getDiscardPile().add(card);
    }
}