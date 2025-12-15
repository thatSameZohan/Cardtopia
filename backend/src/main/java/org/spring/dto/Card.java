package org.spring.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;
@AllArgsConstructor
@Getter @Setter
@ToString
public class Card {

    private final String id;
    private final int attack;
    private final int gold;
    private final int cost;
    private final String ability; // e.g., "DRAW_1", null - placeholder


    public static Card goldCard() {
        return new Card(UUID.randomUUID().toString(), 0, 1, 1, null);
    }

    public static Card attackCard() {
        return new Card(UUID.randomUUID().toString(), 2, 0, 2, null);
    }
}