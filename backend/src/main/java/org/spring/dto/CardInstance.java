package org.spring.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter @Setter
public class CardInstance {

    private final String id = UUID.randomUUID().toString().substring(0, 8);
    private final CardDefinition definition;

    public CardInstance(CardDefinition definition) {
        this.definition = definition;
    }
}
