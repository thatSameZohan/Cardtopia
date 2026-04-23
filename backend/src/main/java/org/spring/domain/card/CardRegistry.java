package org.spring.domain.card;

import org.spring.dto.CardDefinition;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.Map;

@Component
public class CardRegistry {

    private final Map<CardCode, CardDefinition> cards;

    public CardRegistry() {

        this.cards = new EnumMap<>(CardCode.class);

        CardDefinitions.CORE_SET.forEach(this::register);

        }

    public CardDefinition get(CardCode code) {
        CardDefinition def = cards.get(code);
        if (def == null) {
            throw new IllegalArgumentException("Card not found: " + code);
        }
        return def;
    }

    public Map<CardCode, CardDefinition> getAll() {
        return cards;
    }

    private void register(CardDefinition def){
        if (cards.put(def.getCode(), def) != null) {
            throw new IllegalStateException("Duplicate card: " + def.getCode());
        }
    }
}