package org.spring.domain.game;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.spring.domain.card.CardCode;
import org.spring.domain.card.CardFaction;
import org.spring.domain.deck.DeckService;
import org.spring.domain.effect.EffectService;
import org.spring.dto.GameState;
import org.spring.dto.PlayerState;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class TurnService {

    private final DeckService deckService;
    private final EffectService effectService;

    public void endTurn(GameState gs, PlayerState player){
//        player.setCurrentGold(0);
        player.setCurrentAttack(0);
        player.setRightExile(0);
        player.setDestroyBase(0);
        player.setTopDeckNextShip(0);

        player.getPlayedCards().stream()
                .filter(c -> c.getCode().equals(CardCode.CORE_MERCENARY))
                .forEach(c -> c.setCardFaction(CardFaction.NEUTRAL));

        player.getDiscardPile().addAll(player.getPlayedCards());
        player.getPlayedCards().clear();

        // Определяем следующего игрока
        PlayerState nextPlayer = gs.getOpponent(player.getPlayerId());

        gs.setActivePlayerId(nextPlayer.getPlayerId());

        if (nextPlayer.getHand().isEmpty()) {

            deckService.draw(nextPlayer, 5);

        }

        activateStructures(nextPlayer, gs);

        log.info("Ход завершен для игрока {}", player.getPlayerId());
    }

    private void activateStructures(PlayerState player, GameState gs) {
        player.getBases().forEach(c ->
                effectService.applyPlayEffects(c, player, gs)
        );
        player.getOutposts().forEach(c ->
                effectService.applyPlayEffects(c, player, gs)
        );
    }
}
