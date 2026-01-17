package org.spring.util;

import org.spring.dto.CardDefinition;
import org.spring.dto.EffectDto;
import org.spring.dto.SetEffects;
import org.spring.enums.CardType;
import org.spring.enums.EffectType;
import org.spring.enums.Faction;
import org.spring.enums.SetEffectsType;

import java.util.ArrayList;
import java.util.List;

public class CardDefinitionUtil {

    public static CardDefinition getViper(){
        return new CardDefinition("CORE_VIPER",4,"Гадюка", CardType.SHIP, Faction.NEUTRAL, 0,
                new SetEffects(List.of(new EffectDto(EffectType.COMBAT, 1)), SetEffectsType.AND),
                null,
                null);
    }

    public static CardDefinition getScout(){
        return new CardDefinition("CORE_SCOUT",16,"Разведчик", CardType.SHIP, Faction.NEUTRAL, 0,
                new SetEffects(List.of(new EffectDto(EffectType.TRADE, 1)), SetEffectsType.AND),
                null,
                null);
    }

    public static CardDefinition getExplorer(){
        return new CardDefinition("CORE_EXPLORER", 10,"Пионер", CardType.SHIP, Faction.NEUTRAL, 0,
                new SetEffects(List.of(new EffectDto(EffectType.TRADE, 2)), SetEffectsType.AND),
                null,
                new SetEffects(List.of(new EffectDto(EffectType.COMBAT, 2)), SetEffectsType.AND));
    }

    public static List<CardDefinition> getCoreSet(){
        List<CardDefinition> coreSet = new ArrayList<>();
        coreSet.add(new CardDefinition("CORE_FLAGSHIP",10, "Флагман", CardType.SHIP, Faction.TRADE_FEDERATION, 6,
                new SetEffects(List.of(new EffectDto(EffectType.COMBAT, 5), new EffectDto(EffectType.DRAW, 1)), SetEffectsType.AND),
                new SetEffects(List.of(new EffectDto(EffectType.HEALTH, 5)), SetEffectsType.AND),
                null));
        coreSet.add(new CardDefinition("CORE_SHUTTLE",30, "Челнок", CardType.SHIP, Faction.TRADE_FEDERATION, 1,
                new SetEffects(List.of(new EffectDto(EffectType.TRADE, 2)), SetEffectsType.AND),
                new SetEffects(List.of(new EffectDto(EffectType.HEALTH, 4)), SetEffectsType.AND),
                null));
        coreSet.add(new CardDefinition("CORE_DREADNOUGHT",10, "Дредноут", CardType.SHIP, Faction.STAR_EMPIRE, 7,
                new SetEffects(List.of(new EffectDto(EffectType.COMBAT, 7), new EffectDto(EffectType.DRAW, 1)), SetEffectsType.AND),
                null,
                new SetEffects(List.of(new EffectDto(EffectType.COMBAT, 5)), SetEffectsType.AND)));
        return coreSet;
    }

}
