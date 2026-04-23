package org.spring.mapper;

import org.spring.dto.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ViewMapper {

    public PrivatePlayerView toPrivatePlayerView (PlayerState player){
        return new PrivatePlayerView (player.getHand(), player.getDiscardPile());
    }

    public GameView toGameView(GameState gs) {

        List<PlayerView> players = gs.getPlayers().values().stream()
                .map(p -> toPlayerView(p, gs))
                .toList();

        return new GameView(
                gs.getId(),
                gs.getActivePlayerId(),
                gs.getStatus(),
                players,
                List.copyOf(gs.getMarket()),
                gs.getMarketDeck().size(),
                gs.getWinnerId(),
                gs.getExplorerPile()
        );
    }

    private PlayerView toPlayerView(PlayerState p, GameState gs) {
        return new PlayerView(
                p.getPlayerId(),
                p.getCurrentAttack(),
                p.getCurrentGold(),
                p.getHealth(),
                p.getForcedDiscard(),
                p.getRightExile(),
                p.getDestroyBase(),
                p.getTopDeckNextShip(),
                p.getBuyFreeTopDeck(),
                p.getDeck().size(),
                p.getDiscardPile().size(),
                p.getHand().size(),
                List.copyOf(p.getPlayedCards()),
                List.copyOf(p.getBases()),
                List.copyOf(p.getOutposts()),
                p.getPlayerId().equals(gs.getActivePlayerId())
        );
    }
}
