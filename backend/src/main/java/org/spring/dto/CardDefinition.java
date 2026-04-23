package org.spring.dto;

import lombok.*;
import org.spring.domain.card.CardCode;
import org.spring.domain.card.CardType;
import org.spring.domain.card.CardFaction;

@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
public class CardDefinition {

    private CardCode code;
    private int copies;
    private String name;
    private CardType type;
    private CardFaction cardFaction;
    private int cost;
    private int defense;

    private SetEffects mainEffects;
    private SetEffects factionEffectsLvl1;
    private SetEffects scrapEffects;
}