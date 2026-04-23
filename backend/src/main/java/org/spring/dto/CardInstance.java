package org.spring.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.spring.domain.card.CardCode;
import org.spring.domain.card.CardType;
import org.spring.domain.card.CardFaction;

@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
public class CardInstance {

    private String id;
    private CardCode code;
    private String name;
    private CardType type;
    private CardFaction cardFaction;
    private int cost;
    private int defense;

    private SetEffects mainEffects;
    private SetEffects factionEffectsLvl1;
    private SetEffects scrapEffects;

}
