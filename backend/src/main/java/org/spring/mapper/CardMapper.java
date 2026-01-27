package org.spring.mapper;

import org.spring.dto.CardDefinition;
import org.spring.dto.CardInstance;
import org.springframework.stereotype.Component;

@Component
public class CardMapper {

    public CardInstance toInstance (CardDefinition def) {

        return new CardInstance(
                def.getCode(),
                def.getName(),
                def.getType(),
                def.getFaction(),
                def.getCost(),
                def.getDefense(),
                def.getMainEffects(),
                def.getFactionEffectsLvl1(),
                def.getScrapEffects()
        );
    }
}

