package org.spring.domain.market.impl;

import lombok.RequiredArgsConstructor;
import org.spring.domain.card.CardType;
import org.spring.domain.market.PurchaseStrategy;
import org.spring.domain.market.PurchaseType;
import org.spring.domain.validators.GameActionValidator;
import org.spring.dto.CardInstance;
import org.spring.dto.GameState;
import org.spring.dto.PlayerState;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class TopDeckOptionalStrategy implements PurchaseStrategy {

    private final GameActionValidator gameActionValidator;

    @Override
    public PurchaseType getType() {
        return PurchaseType.TOP_DECK_OPTIONAL;
    }

    @Override
    public void buy(CardInstance card, PlayerState player, GameState gs) {

        gameActionValidator.validateGoldForBuy(player, card);
        gameActionValidator.validateRightBuyTopDeck(player);
        gameActionValidator.validateIsShip(card);

        player.setCurrentGold(player.getCurrentGold() - card.getCost());
        player.getDeck().addFirst(card);
        player.setTopDeckNextShip(player.getTopDeckNextShip() - 1);

    }
}