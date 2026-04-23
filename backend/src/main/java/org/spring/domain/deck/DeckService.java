package org.spring.domain.deck;

import org.spring.dto.PlayerState;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class DeckService {

    public void draw(PlayerState player, int count) {
        for (int i = 0; i < count; i++) {

            reshuffleIfNeeded(player);

            if (player.getDeck().isEmpty()) {
                return;
            }

            player.getHand().add(player.getDeck().removeFirst());
        }
    }

    private void reshuffleIfNeeded(PlayerState player) {
        if (player.getDeck().isEmpty() && !player.getDiscardPile().isEmpty()) {
            Collections.shuffle(player.getDiscardPile());
            player.getDeck().addAll(player.getDiscardPile());
            player.getDiscardPile().clear();
        }
    }
}