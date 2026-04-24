package org.spring.domain.validators;

import org.spring.domain.card.CardType;
import org.spring.dto.CardInstance;
import org.spring.dto.GameState;
import org.spring.dto.PlayerState;
import org.spring.domain.game.GameStatus;
import org.spring.exc.GameCommonException;
import org.springframework.stereotype.Component;

@Component
public class GameActionValidator {

    public void validateGameStatus(GameState gs) {
        if (gs.getStatus() == GameStatus.FINISHED) {
            throw new GameCommonException("GAME_FINISHED", "Игра окончена");
        }
    }

    public void validatePlayerTurn(GameState gs, String playerId) {
        if (!gs.isPlayersTurn(playerId)) {
            throw new GameCommonException("NOT_YOUR_TURN", "Ход другого игрока");
        }
    }

    public void validateNeedForcedDiscard(PlayerState player) {
        if (player.getForcedDiscard() > 0) {
            throw new GameCommonException("NEED_FORCED_DISCARD", "Игрок должен сбросить карту");
        }
    }

    public void validateNoNeedForcedDiscard(PlayerState player) {
        if (player.getForcedDiscard() <= 0) {
            throw new GameCommonException("NO_NEED_FORCED_DISCARD", "Принудительный сброс не нужен");
        }
    }

    public void validateEmptyHand(PlayerState player) {
        if (!player.getHand().isEmpty()) {
            throw new GameCommonException("HAND_NOT_EMPTY", "Игрок должен разыграть все карты в руке");
        }
    }

    public void validateMakeAttack(PlayerState player) {
        if (player.getCurrentAttack() != 0) {
            throw new GameCommonException("MAKE_ATTACK", "Игрок должен совершить атаку");
        }
    }
    public void validateRequiredFreeBuy(PlayerState player, GameState gs) {
        if (player.getBuyFreeTopDeck() > 0 && !gs.getMarket().isEmpty()) {
            throw new GameCommonException("REQUIRED_FREE_BUY", "Игрок должен совершить бесплатную покупку");
        }
    }
    public void validateRightExile(PlayerState player) {
        if (player.getRightExile() <= 0) {
            throw new GameCommonException("NO_RIGHT_EXILE", "У игрока нет права удалить карту");
        }
    }
    public void validateRightDestroyBase (PlayerState player) {
        if (player.getDestroyBase() <= 0) {
            throw new GameCommonException("NO_RIGHT_DESTROY_BASE", "У игрока нет права разрушить базу");
        }
    }
    public void validateBuyFreeTopDeck (PlayerState player) {
        if (player.getBuyFreeTopDeck() <= 0) {
            throw new GameCommonException("NO_FREE_SHIP_TOP_DECK", "Нет права на бесплатную покупку корабля наверх колоды");
        }
    }

    public void validateRightBuyTopDeck (PlayerState player) {
        if (player.getTopDeckNextShip() <= 0) {
            throw new GameCommonException("NO_RIGHT_TOP_DECK", "Нет права покупки наверх колоды");
        }
    }

    public void validateIsShip(CardInstance card){
        if (card.getType() != CardType.SHIP) {
            throw new GameCommonException("NOT_SHIP", "Карта не является кораблем");
        }
    }

    public void validateGoldForBuy(PlayerState player, CardInstance card){
        if (player.getCurrentGold() < card.getCost()) {
            throw new GameCommonException("NOT_ENOUGH_GOLD", "Недостаточно золота для покупки");
        }
    }


}