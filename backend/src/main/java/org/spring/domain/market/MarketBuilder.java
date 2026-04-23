package org.spring.domain.market;

import lombok.RequiredArgsConstructor;
import org.spring.domain.card.CardCode;
import org.spring.domain.card.CardFactory;
import org.spring.domain.card.CardRegistry;
import org.spring.dto.CardInstance;
import org.spring.dto.GameState;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@RequiredArgsConstructor
public class MarketBuilder {

    private final CardRegistry registry;
    private final CardFactory cardFactory;

    public void build(GameState gs) {

        // MARKET DECK
        List<CardInstance> marketDeck = new ArrayList<>(registry.getAll().values().stream()
                .flatMap(def -> cardFactory.expand(def).stream())
                .toList());

        Collections.shuffle(marketDeck);

        gs.getMarketDeck().clear();
        gs.getMarketDeck().addAll(marketDeck);

        // EXPLORER
        CardInstance explorer = cardFactory.expand(
                registry.get(CardCode.CORE_EXPLORER)
        ).getFirst();

        gs.getExplorerPile().clear();
        gs.getExplorerPile().add(explorer);

        // 5 карт на рынок
        gs.getMarket().clear();
        for (int i = 0; i < 5; i++) {
            gs.getMarket().add(gs.getMarketDeck().removeFirst());
        }
    }
}