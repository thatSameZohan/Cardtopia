package org.spring.domain.deck;

import lombok.RequiredArgsConstructor;
import org.spring.domain.card.*;
import org.spring.dto.CardDefinition;
import org.spring.dto.CardInstance;
import org.spring.dto.GameState;
import org.spring.dto.PlayerState;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@RequiredArgsConstructor
public class DeckBuilder {

    private final CardRegistry registry;
    private final CardFactory factory;

    public void buildPlayersDecks(GameState gs) {

        int playersCount = gs.getPlayers().size();

        CardDefinition scout = registry.get(CardCode.CORE_SCOUT);
        CardDefinition viper = registry.get(CardCode.CORE_VIPER);

        for (PlayerState player : gs.getPlayers().values()) {

            List<CardInstance> deck = new ArrayList<>();

            int scoutCount = scout.getCopies() / playersCount;
            int viperCount = viper.getCopies() / playersCount;

            for (int i = 0; i < scoutCount; i++) {
                deck.add(factory.create(CardCode.CORE_SCOUT));
            }

            for (int i = 0; i < viperCount; i++) {
                deck.add(factory.create(CardCode.CORE_VIPER));
            }

            Collections.shuffle(deck);
            player.setDeck(deck);
        }
    }
}