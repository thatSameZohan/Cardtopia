package org.spring.domain.market;

import org.spring.dto.CardInstance;
import org.spring.dto.GameState;
import org.spring.dto.PlayerState;

public interface PurchaseStrategy {

    PurchaseType getType();

    void buy(CardInstance card, PlayerState player, GameState gs);

}