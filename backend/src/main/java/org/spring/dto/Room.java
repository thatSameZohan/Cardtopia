package org.spring.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.LinkedHashMap;
import java.util.Map;

@NoArgsConstructor
@Getter
@Setter
public class Room {

    private String id;

    private String name;

    private final Map<String, PlayerState> players = new LinkedHashMap<>();

    private Boolean isFull;

    public Room(String id, String name, Boolean isFull) {
        this.id = id;
        this.name = name;
        this.isFull = isFull;
    }

    @JsonProperty("participantsCount")
    public int getParticipantsCount() {
        return players.size();
    }

    public boolean isFull() {
        return players.size() >= 2;
    }

    @Override
    public String toString() {
        return "Room{" + "id='" + id + '\'' + ", name='" + name + "', participants=" + getParticipantsCount() + '}';
    }
}