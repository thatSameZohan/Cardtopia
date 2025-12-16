package org.spring.dto;

import org.spring.enums.GameStatus;

import java.util.Collection;

public record CreateRoomResponse (String roomId, String roomName, GameStatus status, Collection<String> playerIds) {}
