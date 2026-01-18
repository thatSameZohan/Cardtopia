package org.spring.dto;

import lombok.*;
import org.spring.enums.CardType;
import org.spring.enums.Faction;

@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
public class CardDefinition {

    private String code;
    private int copies;
    private String name;
    private CardType type;
    private Faction faction;
    private int cost;
    private int defense;

    private SetEffects mainEffects;
    private SetEffects factionEffectsLvl1;
    private SetEffects scrapEffects;
}