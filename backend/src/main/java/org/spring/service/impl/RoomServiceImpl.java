package org.spring.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.spring.dto.GameState;
import org.spring.dto.PlayerState;
import org.spring.dto.Room;
import org.spring.enums.GameStatus;
import org.spring.exc.RoomCommonException;
import org.spring.service.GameService;
import org.spring.service.RoomService;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Сервис управления логикой комнат.
 * <p>
 * Обеспечивает создание комнат, присоединение к комнате, выход из комнаты, удаление комнаты создателем
 * <p>
 * Игра хранится в виде объекта состояние игры {@link GameState} , который включает игроков {@link PlayerState},
 * рынок карт и текущий статус игры {@link GameStatus}.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RoomServiceImpl implements RoomService {

    private final Map<String, Room> rooms = new ConcurrentHashMap<>();
    private final GameService gameService;

    @Override
    public Room createRoom(String creatorName) {

        if (isUserInAnyRoom(creatorName)) {
            log.error("Пользователь уже находится в комнате");
            throw new RoomCommonException("Пользователь уже находится в комнате");
        }

        String roomId = generateRoomId();

        Room room = new Room (roomId, "Комната " + creatorName, false, creatorName);
        room.getPlayers().add(creatorName);
        rooms.put(roomId, room);
        updateRoomState(room);
        log.info("Комната {} создана пользователем {}", roomId, creatorName);
        return room;
    }

    @Override
    public Room joinRoom(String roomId, String username) {

        Room room = getRoomOrThrow(roomId);

        if (room.getPlayers().size() >= 2) {
            throw new RoomCommonException ("Комната заполнена");
        }

        if (room.getPlayers().contains(username)) {
            throw new RoomCommonException ("Вы уже в этой комнате");
        }

        if (isUserInAnyRoom(username)) {
            throw new RoomCommonException ("Вы уже в другой комнате");
        }

        room.getPlayers().add(username);
        updateRoomState(room);
        log.info("Пользователь {} вошел в комнату {}", username, roomId);
        return room;
    }

    @Override
    public GameState startGame(String roomId, String username) {

        Room room = rooms.get(roomId);

        if (room == null) {
            throw new RoomCommonException ("Комната не найдена");
        }

        if (!room.getCreatorName().equals(username)) {
            throw new RoomCommonException ("Начать игру может только создатель");
        }

        if (room.getPlayers().size() < 2) {
            throw new RoomCommonException ("Недостаточно игроков");
        }

        return gameService.createGame(room, username);
    }

    @Override
    public void leaveRoom(String roomId, String username) {

        Room room = getRoomOrThrow(roomId);

        if (!room.getPlayers().remove(username)) {
            throw new RoomCommonException ("Вы не в этой комнате");
        }

        if (room.getPlayers().isEmpty()) {
            rooms.remove(roomId);
            log.info("Комната {} удалена (пустая)", roomId);
        } else {
            updateRoomState(room);
        }
    }

    @Override
    public void deleteRoom(String roomId, String username) {

        Room room = getRoomOrThrow(roomId);

        if (!room.getCreatorName().equals(username)) {
            throw new RoomCommonException ("Вы не можете удалить эту комнату");
        }

        rooms.remove(roomId);
        log.info("Комната {} удалена создателем {}", roomId, username);
    }

    @Override
    public Collection<Room> listRooms() {
        return rooms.values();
    }

    @Override
    public Optional<Room> findRoom(String roomId) {
        return Optional.ofNullable(rooms.get(roomId));
    }

    @Override
    public boolean isUserInAnyRoom(String username) {
        return rooms.values()
                .stream()
                .anyMatch(r -> r.getPlayers().contains(username));
    }

    @Override
    public void leaveAllRooms(String username) {

        rooms.values().forEach(room -> room.getPlayers().remove(username));
        rooms.entrySet().removeIf(e -> e.getValue().getPlayers().isEmpty());
        rooms.values().forEach(this::updateRoomState);

        log.info("Пользователь {} удалён из всех комнат", username);
    }
    /* ========================= UTIL ========================= */

    /** Получить комнату по ID */
    private Room getRoomOrThrow(String roomId) {
        Room room = rooms.get(roomId);
        if (room == null) {
            throw new RoomCommonException("Комнаты не существует");
        }
        return room;
    }
    /** Обновить isFull */
    private void updateRoomState(Room room) {
        room.setIsFull(room.getPlayers().size() >= 2);
    }

    /** Генерация уникального короткого ID комнаты */
    private String generateRoomId() {
        // короткий уникальный id
        return UUID.randomUUID().toString().substring(0, 8);
    }



}
