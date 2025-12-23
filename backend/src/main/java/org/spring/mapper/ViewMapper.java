package org.spring.mapper;

import org.spring.dto.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ViewMapper {

    public PrivatePlayerView toPrivatePlayerView (PlayerState player){
        return new PrivatePlayerView (player.getHand(),player.getPlayedCards());
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
                gs.getWinnerId()
        );
    }

    private PlayerView toPlayerView(PlayerState p, GameState gs) {
        return new PlayerView(
                p.getPlayerId(),
                p.getHealth(),
                p.getHand().size(),
                p.getDiscardPile().size(),
                p.getDeck().size(),
                p.getCurrentAttack(),
                p.getCurrentGold(),
                p.getPlayerId().equals(gs.getActivePlayerId())
        );
    }
}
