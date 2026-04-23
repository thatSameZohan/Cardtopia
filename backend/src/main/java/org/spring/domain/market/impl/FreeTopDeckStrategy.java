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
public class FreeTopDeckStrategy implements PurchaseStrategy {

    private final GameActionValidator gameActionValidator;

    @Override
    public PurchaseType getType() {
        return PurchaseType.FREE_TOP_DECK_REQUIRED;
    }

    @Override
    public void buy(CardInstance card, PlayerState player, GameState gs) {

        gameActionValidator.validateBuyFreeTopDeck(player);
        gameActionValidator.validateIsShip(card);

        player.getDeck().addFirst(card);
        player.setBuyFreeTopDeck(player.getBuyFreeTopDeck() - 1);
    }
}