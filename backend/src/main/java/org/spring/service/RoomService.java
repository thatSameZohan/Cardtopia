package org.spring.service;

import org.spring.dto.GameState;
import org.spring.dto.Room;

import java.util.Collection;

public interface RoomService {

    Room createRoom(String creatorName);

    Room joinRoom(String roomId, String username);

    GameState startGame(String roomId, String username);

    void leaveRoom(String roomId, String username);

    void deleteRoom(String roomId, String username);

    Collection<Room> listRooms();

    boolean isUserInAnyRoom(String username);

    void leaveAllRooms(String username);

}
