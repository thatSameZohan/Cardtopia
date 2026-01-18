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
        return new CardDefinition("CORE_VIPER",4,"Гадюка", CardType.SHIP, Faction.NEUTRAL, 0, 0,
                new SetEffects(List.of(new EffectDto(EffectType.COMBAT, 1)), SetEffectsType.AND),
                null,
                null);
    }

    public static CardDefinition getScout(){
        return new CardDefinition("CORE_SCOUT",16,"Разведчик", CardType.SHIP, Faction.NEUTRAL, 0, 0,
                new SetEffects(List.of(new EffectDto(EffectType.GOLD, 1)), SetEffectsType.AND),
                null,
                null);
    }

    public static CardDefinition getExplorer(){
        return new CardDefinition("CORE_EXPLORER", 10,"Пионер", CardType.SHIP, Faction.NEUTRAL, 2, 0,
                new SetEffects(List.of(new EffectDto(EffectType.GOLD, 2)), SetEffectsType.AND),
                null,
                new SetEffects(List.of(new EffectDto(EffectType.COMBAT, 2)), SetEffectsType.AND));
    }

    public static List<CardDefinition> getCoreSet(){
        List<CardDefinition> coreSet = new ArrayList<>();
        coreSet.add(new CardDefinition("CORE_FLAGSHIP",1, "Флагман", CardType.SHIP, Faction.TRADE_FEDERATION, 6, 0,
                new SetEffects(List.of(new EffectDto(EffectType.COMBAT, 5), new EffectDto(EffectType.DRAW, 1)), SetEffectsType.AND),
                new SetEffects(List.of(new EffectDto(EffectType.HEALTH, 5)), SetEffectsType.AND),
                null));
        coreSet.add(new CardDefinition("CORE_SHUTTLE",3, "Челнок", CardType.SHIP, Faction.TRADE_FEDERATION, 1, 0,
                new SetEffects(List.of(new EffectDto(EffectType.GOLD, 2)), SetEffectsType.AND),
                new SetEffects(List.of(new EffectDto(EffectType.HEALTH, 4)), SetEffectsType.AND),
                null));
        coreSet.add(new CardDefinition("CORE_DREADNOUGHT",1, "Дредноут", CardType.SHIP, Faction.STAR_EMPIRE, 7, 0,
                new SetEffects(List.of(new EffectDto(EffectType.COMBAT, 7), new EffectDto(EffectType.DRAW, 1)), SetEffectsType.AND),
                null,
                new SetEffects(List.of(new EffectDto(EffectType.COMBAT, 5)), SetEffectsType.AND)));
        coreSet.add(new CardDefinition("CORE_TRADEPOST",2, "Торговый пост", CardType.OUTPOST, Faction.TRADE_FEDERATION, 3,4,
                new SetEffects(List.of(new EffectDto(EffectType.HEALTH, 1), new EffectDto(EffectType.GOLD, 1)), SetEffectsType.OR),
                null,
                new SetEffects(List.of(new EffectDto(EffectType.COMBAT, 3)), SetEffectsType.AND)));
        coreSet.add(new CardDefinition("CORE_AIRCRAFT",1, "Авианосец", CardType.OUTPOST, Faction.STAR_EMPIRE, 5,4,
                new SetEffects(List.of(new EffectDto(EffectType.COMBAT, 3)), SetEffectsType.AND),
                new SetEffects(List.of(new EffectDto(EffectType.COMBAT, 4)), SetEffectsType.AND),
                null));
        coreSet.add(new CardDefinition("CORE_EXCHANGER",3, "Обменник", CardType.SHIP, Faction.BLOB, 2,0,
                new SetEffects(List.of(new EffectDto(EffectType.GOLD, 3)), SetEffectsType.AND),
                new SetEffects(List.of(new EffectDto(EffectType.COMBAT, 2)), SetEffectsType.AND),
                null));
        coreSet.add(new CardDefinition("CORE_CARAVAN",1, "Караван", CardType.SHIP, Faction.TRADE_FEDERATION, 5,0,
                new SetEffects(List.of(new EffectDto(EffectType.HEALTH, 4), new EffectDto(EffectType.COMBAT, 4)), SetEffectsType.AND),
                new SetEffects(List.of(new EffectDto(EffectType.DRAW, 1)), SetEffectsType.AND),
                null));
        coreSet.add(new CardDefinition("CORE_ROBOTRADE",3, "Роботорг", CardType.SHIP, Faction.MACHINE_CULT, 1,0,
                new SetEffects(List.of(new EffectDto(EffectType.GOLD, 1), new EffectDto(EffectType.DESTROY, 1)), SetEffectsType.AND), //TODO логика МОЖЕШЬ и DESTROY
                new SetEffects(List.of(new EffectDto(EffectType.COMBAT, 2)), SetEffectsType.AND),
                null));
        coreSet.add(new CardDefinition("CORE_HIVE",1, "Улей", CardType.BASE, Faction.BLOB, 5,5,
                new SetEffects(List.of(new EffectDto(EffectType.COMBAT, 3)), SetEffectsType.AND),
                new SetEffects(List.of(new EffectDto(EffectType.DRAW, 1)), SetEffectsType.AND),
                null));
        return coreSet;
    }

}
