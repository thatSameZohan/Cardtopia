package org.spring.service;

import org.spring.dto.AttackRequest;
import org.spring.dto.GameState;
import org.spring.dto.PlayCardRequest;
import org.spring.dto.Room;

import java.util.Optional;

public interface GameService {

    /**
     * Создаёт игру на основании комнаты.
     * @param room комната
     * @param creatorName имя создателя комнаты из principal
     * @return объект {@link GameState} с созданной комнатой
     */
    GameState createGame (Room room, String creatorName);

    /**
     * Игрок разыгрывает карту.
     * @param gs объект {@link GameState} комнаты
     * @param playerId ID игрока
     */
    void playCard(GameState gs, String playerId, PlayCardRequest req);

    /**
     * Игрок покупает карту из рынка.
     * @param gs           объект {@link GameState} комнаты
     * @param playerId     ID игрока
     * @param marketCardId ID карты в рынке
     */
    void buyCard (GameState gs, String playerId, String marketCardId, boolean topDeck);

    /**
     * Игрок совершает атаку.
     * @param gs объект {@link GameState} комнаты
     * @param playerId ID игрока
     */
    void attack(GameState gs, String playerId, AttackRequest req);

    /**
     * Завершает ход игрока и передаёт ход следующему.
     * @param gs       объект {@link GameState} комнаты
     * @param playerId ID игрока
     */
    void endTurn(GameState gs, String playerId);

    void scrapStructure(GameState gs, String playerId, String cardId);

    void exileCard(GameState gs, String playerId, String cardId, String cardCode);

    void forceDiscard(GameState gs, String playerId, String cardId);

    void destroyBase(GameState gs, String playerId, String baseId);

    void buyFreeTopDeck (GameState gs, String playerId, String marketCardId);

    /**
     * Находит игровую комнату по её ID.
     * @param gameId ID комнаты
     * @return {@link Optional} с объектом {@link GameState} или пустой, если комната не найдена
     */
    Optional<GameState> findGame (String gameId);
}
