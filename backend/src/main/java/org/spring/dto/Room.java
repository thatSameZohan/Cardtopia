package org.spring.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
public class Room {

    private String id;

    private String name;

    private final Set<String> participants = ConcurrentHashMap.newKeySet();

    private final List<String> turnOrder = new CopyOnWriteArrayList<>();

    private int turnIndex = 0;

    public Room(String id, String name) { this.id = id; this.name = name; }


    @JsonProperty("participantsCount")
    public int getParticipantsCount() { return participants.size(); }
    public boolean isFull() { return participants.size() >= 2; }

    public void addParticipant(String sessionId) {
        participants.add(sessionId);
        if (!turnOrder.contains(sessionId)) turnOrder.add(sessionId);
    }

    public void removeParticipant(String sessionId) {
        participants.remove(sessionId);
        turnOrder.remove(sessionId);
        if (turnIndex >= turnOrder.size()) turnIndex = 0;
    }

    public String getCurrentTurnPlayer() {
        if (turnOrder.isEmpty()) return null;
        return turnOrder.get(turnIndex);
    }

    public void nextTurn() {
        if (!turnOrder.isEmpty()) turnIndex = (turnIndex + 1) % turnOrder.size();
    }

    @Override
    public String toString() {
        return "Room{" + "id='" + id + '\'' + ", name='" + name + "', participants=" + getParticipantsCount() + '}';
    }
}