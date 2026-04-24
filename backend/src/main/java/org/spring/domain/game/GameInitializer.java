package org.spring.domain.game;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.spring.domain.deck.DeckBuilder;
import org.spring.domain.deck.DeckService;
import org.spring.domain.market.MarketBuilder;
import org.spring.dto.GameState;
import org.spring.dto.PlayerState;
import org.springframework.stereotype.Component;

import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class GameInitializer {

    private final DeckService deckService;
    private final DeckBuilder deckBuilder;
    private final MarketBuilder marketBuilder;

    public void init(GameState gs) {

        log.info("Инициализация игры...");

        // 1. рынок + explorer
        marketBuilder.build(gs);

        // 2. стартовые колоды игроков
        deckBuilder.buildPlayersDecks(gs);

        // 3. стартовые руки
        assignStartingHands(gs);

        log.info("Инициализация завершена");
    }

    private void assignStartingHands(GameState gs) {

        List<String> ids = new ArrayList<>(gs.getPlayers().keySet());
        String first = ids.get(new Random().nextInt(ids.size()));
        gs.setActivePlayerId(first);

        for (PlayerState player : gs.getPlayers().values()) {
            player.setCurrentGold(100); // немного голды насыпал каждому для теста
            if (player.getPlayerId().equals(first)) {
                deckService.draw(player, 3);
            } else {
                deckService.draw(player, 5);
            }
        }
    }
}