package org.spring.domain.card;

import lombok.RequiredArgsConstructor;
import org.spring.dto.CardDefinition;
import org.spring.dto.CardInstance;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CardFactory {

    private final CardRegistry registry;

    public CardInstance create(CardCode code) {
        CardDefinition def = registry.get(code);

        return new CardInstance(
                UUID.randomUUID().toString(),
                def.getCode(),
                def.getName(),
                def.getType(),
                def.getCardFaction(),
                def.getCost(),
                def.getDefense(),
                def.getMainEffects(),
                def.getFactionEffectsLvl1(),
                def.getScrapEffects()
        );
    }

    public List<CardInstance> expand(CardDefinition def) {
        List<CardInstance> list = new ArrayList<>();
        for (int i = 0; i < def.getCopies(); i++) {
            list.add(create(def.getCode()));
        }
        return list;
    }
}