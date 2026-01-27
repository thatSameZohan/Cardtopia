package org.spring.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.spring.enums.CardType;
import org.spring.enums.Faction;

import java.util.UUID;
@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
public class CardInstance {

    private final String id = UUID.randomUUID().toString().substring(0, 8);
    private String code;
    private String name;
    private CardType type;
    private Faction faction;
    private int cost;
    private int defense;

    private SetEffects mainEffects;
    private SetEffects factionEffectsLvl1;
    private SetEffects scrapEffects;
}
