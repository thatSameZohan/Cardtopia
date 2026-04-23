package org.spring.domain.market;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.spring.domain.card.CardCode;
import org.spring.dto.CardInstance;
import org.spring.dto.GameState;
import org.spring.dto.PlayerState;
import org.spring.exc.GameCommonException;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
@Slf4j
public class MarketService {

    private final PurchaseStrategyResolver resolver;

    public void buyCard(GameState gs, PlayerState player, String cardId, PurchaseType type) {

        CardInstance card = findCard(gs, cardId);

        PurchaseStrategy strategy = resolver.resolve(type);

        strategy.buy(card, player, gs);

        removeFromSource(gs, card);

        log.info("Игрок {} купил карту {} способом {}", player.getPlayerId(), card.getName(), type);
    }

    private CardInstance findCard(GameState gs, String cardId) {
        return Stream.concat(gs.getMarket().stream(), gs.getExplorerPile().stream())
                .filter(c -> c.getId().equals(cardId))
                .findFirst()
                .orElseThrow(() -> new GameCommonException("CARD_NOT_AVAILABLE", "Карты нет"));
    }

    private void removeFromSource(GameState gs, CardInstance card) {
        if (card.getCode() == CardCode.CORE_EXPLORER) {
            gs.getExplorerPile().remove(card);
        } else {
            gs.getMarket().remove(card);
            refillMarket(gs);
        }
    }

    private void refillMarket(GameState gs) {
        if (!gs.getMarketDeck().isEmpty()) {
            gs.getMarket().add(gs.getMarketDeck().removeFirst());
        }
    }
}