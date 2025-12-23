package org.spring.mapper;

import org.spring.dto.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GameViewMapper {

    public GameView toView(GameState gs, String viewer) {

        List<PlayerView> players = gs.getPlayers().values().stream()
                .map(p -> toPlayerView(p, gs))
                .toList();

        PlayerState me = gs.getPlayers().get(viewer);

        PlayerPrivateView you = me == null ? null :
                new PlayerPrivateView(
                        List.copyOf(me.getHand()),
                        List.copyOf(me.getPlayedCards())
                );

        return new GameView(
                gs.getId(),
                gs.getActivePlayerId(),
                gs.getStatus(),
                players,
                List.copyOf(gs.getMarket()),
                you,
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
