package org.spring.domain.card;

import org.spring.dto.CardDefinition;
import org.spring.dto.EffectDto;
import org.spring.dto.SetEffects;
import org.spring.domain.effect.EffectType;
import org.spring.domain.effect.SetEffectsType;

import java.util.List;

public final class CardDefinitions {

    public static final List<CardDefinition> CORE_SET = List.of(

        new CardDefinition(CardCode.CORE_VIPER,4,"Гадюка", CardType.SHIP, CardFaction.NEUTRAL, 0, 0,
                new SetEffects(List.of(new EffectDto(EffectType.COMBAT, 1)), SetEffectsType.AND),
                null,
                null),

        new CardDefinition(CardCode.CORE_SCOUT,16,"Разведчик", CardType.SHIP, CardFaction.NEUTRAL, 0, 0,
                new SetEffects(List.of(new EffectDto(EffectType.GOLD, 1)), SetEffectsType.AND),
                null,
                null),

        new CardDefinition(CardCode.CORE_EXPLORER, 10,"Пионер", CardType.SHIP, CardFaction.NEUTRAL, 2, 0,
                new SetEffects(List.of(new EffectDto(EffectType.GOLD, 2)), SetEffectsType.AND),
                null,
                new SetEffects(List.of(new EffectDto(EffectType.COMBAT, 2)), SetEffectsType.AND)),

        new CardDefinition(CardCode.CORE_FLAGSHIP,1,"Флагман", CardType.SHIP, CardFaction.TRADE_FEDERATION,6,0,
                new SetEffects(List.of(new EffectDto(EffectType.COMBAT,5), new EffectDto(EffectType.DRAW,1)), SetEffectsType.AND),
                new SetEffects(List.of(new EffectDto(EffectType.HEALTH,5)), SetEffectsType.AND),
                null),

        new CardDefinition(CardCode.CORE_SHUTTLE,3,"Челнок", CardType.SHIP, CardFaction.TRADE_FEDERATION,1,0,
                new SetEffects(List.of(new EffectDto(EffectType.GOLD,2)), SetEffectsType.AND),
                new SetEffects(List.of(new EffectDto(EffectType.HEALTH,4)), SetEffectsType.AND),
                null),

        new CardDefinition(CardCode.CORE_DREADNOUGHT,1,"Дредноут", CardType.SHIP, CardFaction.STAR_EMPIRE,7,0,
                new SetEffects(List.of(new EffectDto(EffectType.COMBAT,7), new EffectDto(EffectType.DRAW,1)), SetEffectsType.AND),
                null,
                new SetEffects(List.of(new EffectDto(EffectType.COMBAT,5)), SetEffectsType.AND)),

        new CardDefinition(CardCode.CORE_TRADEPOST,2,"Торговый пост", CardType.OUTPOST, CardFaction.TRADE_FEDERATION,3,4,
                new SetEffects(List.of(new EffectDto(EffectType.HEALTH,1), new EffectDto(EffectType.GOLD,1)), SetEffectsType.OR),
                null,
                new SetEffects(List.of(new EffectDto(EffectType.COMBAT,3)), SetEffectsType.AND)),

        new CardDefinition(CardCode.CORE_AIRCRAFT,1,"Авианосец", CardType.OUTPOST, CardFaction.STAR_EMPIRE,5,4,
                new SetEffects(List.of(new EffectDto(EffectType.COMBAT,3)), SetEffectsType.AND),
                new SetEffects(List.of(new EffectDto(EffectType.COMBAT,4)), SetEffectsType.AND),
                null),

        new CardDefinition(CardCode.CORE_EXCHANGER,3,"Обменник", CardType.SHIP, CardFaction.BLOB,2,0,
                new SetEffects(List.of(new EffectDto(EffectType.GOLD,3)), SetEffectsType.AND),
                new SetEffects(List.of(new EffectDto(EffectType.COMBAT,2)), SetEffectsType.AND),
                null),

        new CardDefinition(CardCode.CORE_CARAVAN,1,"Караван", CardType.SHIP, CardFaction.TRADE_FEDERATION,5,0,
                new SetEffects(List.of(new EffectDto(EffectType.HEALTH,4), new EffectDto(EffectType.COMBAT,4)), SetEffectsType.AND),
                new SetEffects(List.of(new EffectDto(EffectType.DRAW,1)), SetEffectsType.AND),
                null),

        new CardDefinition(CardCode.CORE_ROBOT_TRADE,3,"Роботорг", CardType.SHIP, CardFaction.MACHINE_CULT,1,0,
                new SetEffects(List.of(new EffectDto(EffectType.GOLD,1), new EffectDto(EffectType.EXILE,1)), SetEffectsType.AND),
                new SetEffects(List.of(new EffectDto(EffectType.COMBAT,2)), SetEffectsType.AND),
                null),

        new CardDefinition(CardCode.CORE_HIVE,1,"Улей", CardType.BASE, CardFaction.BLOB,5,5,
                new SetEffects(List.of(new EffectDto(EffectType.COMBAT,3)), SetEffectsType.AND),
                new SetEffects(List.of(new EffectDto(EffectType.DRAW,1)), SetEffectsType.AND),
                null),

        new CardDefinition(CardCode.CORE_ROBOT_SUPPLE,3,"Робоснаб", CardType.SHIP, CardFaction.MACHINE_CULT,3,0,
                new SetEffects(List.of(new EffectDto(EffectType.GOLD,2), new EffectDto(EffectType.EXILE,1)), SetEffectsType.AND),
                new SetEffects(List.of(new EffectDto(EffectType.COMBAT,2)), SetEffectsType.AND),
                null),

        new CardDefinition(CardCode.CORE_RESCUER,3,"Спасатель", CardType.SHIP, CardFaction.STAR_EMPIRE,3,0,
                new SetEffects(List.of(new EffectDto(EffectType.GOLD,1), new EffectDto(EffectType.DRAW,1)), SetEffectsType.AND),
                null,
                new SetEffects(List.of(new EffectDto(EffectType.FORCE_DISCARD,1)), SetEffectsType.AND)),

        new CardDefinition(CardCode.CORE_ORBITAL_STATION,2,"Орбитальная станция", CardType.OUTPOST, CardFaction.STAR_EMPIRE,4,4,
                new SetEffects(List.of(new EffectDto(EffectType.COMBAT,2)), SetEffectsType.AND),
                new SetEffects(List.of(new EffectDto(EffectType.COMBAT,2)), SetEffectsType.AND),
                new SetEffects(List.of(new EffectDto(EffectType.GOLD,4)), SetEffectsType.AND)),

        new CardDefinition(CardCode.CORE_CARBAGE_FABRIC,2,"Мусорозавод", CardType.OUTPOST, CardFaction.STAR_EMPIRE,4,4,
                new SetEffects(List.of(new EffectDto(EffectType.GOLD,1), new EffectDto(EffectType.DISCARD_DRAW,2)), SetEffectsType.OR),
                null,
                null),

        new CardDefinition(CardCode.CORE_LEECH,2,"Пиявка", CardType.SHIP, CardFaction.BLOB,3,0,
                new SetEffects(List.of(new EffectDto(EffectType.COMBAT,5)), SetEffectsType.AND),
                new SetEffects(List.of(new EffectDto(EffectType.COMBAT,2)), SetEffectsType.AND),
                new SetEffects(List.of(new EffectDto(EffectType.GOLD,3)), SetEffectsType.AND)),

        new CardDefinition(CardCode.CORE_SUPREME_COUNCIL,1,"Верховный Совет", CardType.OUTPOST, CardFaction.TRADE_FEDERATION,6,6,
                new SetEffects(List.of(new EffectDto(EffectType.GOLD,3)), SetEffectsType.AND),
                null,
                new SetEffects(List.of(new EffectDto(EffectType.DRAW,1), new EffectDto(EffectType.DESTROY_BASE,1)), SetEffectsType.AND)),

        new CardDefinition(CardCode.CORE_CITADEL,1,"Цитадель", CardType.OUTPOST, CardFaction.STAR_EMPIRE,6,6,
                new SetEffects(List.of(new EffectDto(EffectType.COMBAT,3)), SetEffectsType.AND),
                new SetEffects(List.of(new EffectDto(EffectType.FORCE_DISCARD,1)), SetEffectsType.AND),
                null),

        new CardDefinition(CardCode.CORE_MECHANICAL_INSPECTION,2,"Мехдозор", CardType.SHIP, CardFaction.MACHINE_CULT,4,0,
                new SetEffects(List.of(new EffectDto(EffectType.GOLD,3), new EffectDto(EffectType.COMBAT,5)), SetEffectsType.OR),
                new SetEffects(List.of(new EffectDto(EffectType.EXILE,1)), SetEffectsType.AND),
                null),

        new CardDefinition(CardCode.CORE_SHARK,1,"Акула", CardType.SHIP, CardFaction.BLOB,7,0,
                new SetEffects(List.of(new EffectDto(EffectType.COMBAT,6), new EffectDto(EffectType.DRAW,1)), SetEffectsType.AND),
                new SetEffects(List.of(new EffectDto(EffectType.DRAW,1)), SetEffectsType.AND),
                null),

        new CardDefinition(CardCode.CORE_BULLDOZER,1,"Бульдозер", CardType.SHIP, CardFaction.MACHINE_CULT,6,0,
                new SetEffects(List.of(new EffectDto(EffectType.COMBAT,6), new EffectDto(EffectType.DESTROY_BASE,1)), SetEffectsType.AND),
                new SetEffects(List.of(new EffectDto(EffectType.DRAW,1)), SetEffectsType.AND),
                null),

        new CardDefinition(CardCode.CORE_MERCENARY,2,"Наемник", CardType.SHIP, CardFaction.NEUTRAL,3,0,
                new SetEffects(List.of(new EffectDto(EffectType.COMBAT,5), new EffectDto(EffectType.SELECT_FACTION,1)), SetEffectsType.AND),
                null,
                null),

        new CardDefinition(CardCode.CORE_BURNER,3,"Выжигатель", CardType.SHIP, CardFaction.MACHINE_CULT,2,0,
                new SetEffects(List.of(new EffectDto(EffectType.COMBAT,2), new EffectDto(EffectType.EXILE,1)), SetEffectsType.AND),
                new SetEffects(List.of(new EffectDto(EffectType.COMBAT,2)), SetEffectsType.AND),
                null),

        new CardDefinition(CardCode.CORE_MACHINE_BASE,1,"Машинобаза", CardType.OUTPOST, CardFaction.MACHINE_CULT,7,6,
                new SetEffects(List.of(new EffectDto(EffectType.DRAW,1), new EffectDto(EffectType.EXILE,1)), SetEffectsType.AND),
                null,
                null),

        new CardDefinition(CardCode.CORE_DUMP,1,"Свалка", CardType.OUTPOST, CardFaction.MACHINE_CULT,6,5,
                new SetEffects(List.of(new EffectDto(EffectType.EXILE,1)), SetEffectsType.AND),
                null,
                null),

        new CardDefinition(CardCode.CORE_FRIGATE,3,"Фрегат", CardType.SHIP, CardFaction.STAR_EMPIRE,3,0,
                new SetEffects(List.of(new EffectDto(EffectType.COMBAT,4), new EffectDto(EffectType.FORCE_DISCARD,1)), SetEffectsType.AND),
                new SetEffects(List.of(new EffectDto(EffectType.COMBAT,2)), SetEffectsType.AND),
                new SetEffects(List.of(new EffectDto(EffectType.EMPTY,0)), SetEffectsType.AND)),

        new CardDefinition(CardCode.CORE_SWIFT,3,"Стриж", CardType.SHIP, CardFaction.STAR_EMPIRE,1,0,
                new SetEffects(List.of(new EffectDto(EffectType.COMBAT,2), new EffectDto(EffectType.FORCE_DISCARD,1)), SetEffectsType.AND),
                new SetEffects(List.of(new EffectDto(EffectType.COMBAT,2)), SetEffectsType.AND),
                null),

        new CardDefinition(CardCode.CORE_TRAIN,2,"Товарняк", CardType.SHIP, CardFaction.TRADE_FEDERATION,4,0,
                new SetEffects(List.of(new EffectDto(EffectType.GOLD,4)), SetEffectsType.AND),
                new SetEffects(List.of(new EffectDto(EffectType.TOP_DECK_NEXT_SHIP,1)), SetEffectsType.AND),
                null),

        new CardDefinition(CardCode.CORE_HQ,1,"Штаб", CardType.BASE, CardFaction.STAR_EMPIRE,8,8,
                new SetEffects(List.of(new EffectDto(EffectType.INCREASE_COMBAT,1)), SetEffectsType.AND),
                null,
                null),

        new CardDefinition(CardCode.CORE_YACHT,2,"Яхта", CardType.SHIP, CardFaction.TRADE_FEDERATION,4,0,
                new SetEffects(List.of(new EffectDto(EffectType.HEALTH,3), new EffectDto(EffectType.GOLD,2),
                        new EffectDto(EffectType.DRAW_CONDITIONAL_BASE,2)), SetEffectsType.AND),
                null,
                null),

        new CardDefinition(CardCode.CORE_BORDER_OUTPOST,1,"Погранзастава", CardType.OUTPOST, CardFaction.TRADE_FEDERATION,5,5,
                new SetEffects(List.of(new EffectDto(EffectType.HEALTH,3), new EffectDto(EffectType.COMBAT,2)), SetEffectsType.OR),
                new SetEffects(List.of(new EffectDto(EffectType.COMBAT,2)), SetEffectsType.AND),
                null),

        new CardDefinition(CardCode.CORE_IRON,3,"Утюг", CardType.SHIP, CardFaction.TRADE_FEDERATION,2,0,
                new SetEffects(List.of(new EffectDto(EffectType.HEALTH,4), new EffectDto(EffectType.GOLD,2)), SetEffectsType.AND),
                new SetEffects(List.of(new EffectDto(EffectType.COMBAT,4)), SetEffectsType.AND),
                null),

        new CardDefinition(CardCode.CORE_CORVETTE,2,"Корвет", CardType.SHIP, CardFaction.STAR_EMPIRE,2,0,
                new SetEffects(List.of(new EffectDto(EffectType.COMBAT,1), new EffectDto(EffectType.DRAW,1)), SetEffectsType.AND),
                new SetEffects(List.of(new EffectDto(EffectType.COMBAT,2)), SetEffectsType.AND),
                null),

        new CardDefinition(CardCode.CORE_BRAIN,1,"Мозг", CardType.OUTPOST, CardFaction.MACHINE_CULT,8,6,
                new SetEffects(List.of(new EffectDto(EffectType.EXILE_DRAW,2)), SetEffectsType.AND),
                null,
                null),

        new CardDefinition(CardCode.CORE_CONTROL,1,"Управление", CardType.BASE, CardFaction.TRADE_FEDERATION,7,6,
                new SetEffects(List.of(new EffectDto(EffectType.GOLD,2), new EffectDto(EffectType.TOP_DECK_NEXT_SHIP,1)), SetEffectsType.AND),
                new SetEffects(List.of(new EffectDto(EffectType.DRAW,1)), SetEffectsType.AND),
                null),

        new CardDefinition(CardCode.CORE_COMMANDER,1,"Командор", CardType.SHIP, CardFaction.TRADE_FEDERATION,8,0,
                new SetEffects(List.of(new EffectDto(EffectType.HEALTH,4), new EffectDto(EffectType.COMBAT,5), new EffectDto(EffectType.DRAW,2)), SetEffectsType.AND),
                new SetEffects(List.of(new EffectDto(EffectType.DESTROY_BASE,1)), SetEffectsType.AND),
                null),

        new CardDefinition(CardCode.CORE_TADPOLE,3,"Головастик", CardType.SHIP, CardFaction.BLOB,1,0,
                new SetEffects(List.of(new EffectDto(EffectType.COMBAT,3)), SetEffectsType.AND),
                new SetEffects(List.of(new EffectDto(EffectType.DRAW,1)), SetEffectsType.AND),
                null),

        new CardDefinition(CardCode.CORE_MEDUSA,3,"Медуза", CardType.BASE, CardFaction.BLOB,3,5,
                new SetEffects(List.of(new EffectDto(EffectType.COMBAT,1)), SetEffectsType.AND),
                null,
                new SetEffects(List.of(new EffectDto(EffectType.GOLD,3)), SetEffectsType.AND)),

        new CardDefinition(CardCode.CORE_RHINO,2,"Носорог", CardType.SHIP, CardFaction.BLOB,4,0,
                new SetEffects(List.of(new EffectDto(EffectType.COMBAT,6)), SetEffectsType.AND),
                new SetEffects(List.of(new EffectDto(EffectType.DESTROY_BASE,1), new EffectDto(EffectType.EXILE,1)), SetEffectsType.AND),
                null),

        new CardDefinition(CardCode.CORE_TURTLE,1,"Черепаха", CardType.SHIP, CardFaction.BLOB,6,0,
                new SetEffects(List.of(new EffectDto(EffectType.COMBAT,7)), SetEffectsType.AND),
                new SetEffects(List.of(new EffectDto(EffectType.BUY_FREE_TOP_DECK,1)), SetEffectsType.AND),
                null),

        new CardDefinition(CardCode.CORE_BATTLESHIP,3,"Линкор", CardType.SHIP, CardFaction.STAR_EMPIRE,6,0,
                new SetEffects(List.of(new EffectDto(EffectType.COMBAT,5), new EffectDto(EffectType.DRAW,1)), SetEffectsType.AND),
                new SetEffects(List.of(new EffectDto(EffectType.FORCE_DISCARD,1)), SetEffectsType.AND),
                new SetEffects(List.of(new EffectDto(EffectType.DRAW,1), new EffectDto(EffectType.DESTROY_BASE,1)), SetEffectsType.AND)),

        new CardDefinition(CardCode.CORE_WHIRLIGIG,2,"Юла", CardType.OUTPOST, CardFaction.MACHINE_CULT,3,5,
                null,
                null,
                new SetEffects(List.of(new EffectDto(EffectType.COMBAT,5)), SetEffectsType.AND)),

        new CardDefinition(CardCode.CORE_DRAGONFLY,1,"Стрекоза", CardType.SHIP, CardFaction.BLOB,6,0,
                new SetEffects(List.of(new EffectDto(EffectType.COMBAT,8)), SetEffectsType.AND),
                new SetEffects(List.of(new EffectDto(EffectType.DRAW,1)), SetEffectsType.AND),
                new SetEffects(List.of(new EffectDto(EffectType.COMBAT,4)), SetEffectsType.AND)),

        new CardDefinition(CardCode.CORE_TANK,1,"Танк", CardType.SHIP, CardFaction.MACHINE_CULT,5,0,
                new SetEffects(List.of(new EffectDto(EffectType.COMBAT,4), new EffectDto(EffectType.EXILE,1)), SetEffectsType.AND),
                new SetEffects(List.of(new EffectDto(EffectType.DRAW,1)), SetEffectsType.AND),
                null),

        new CardDefinition(CardCode.CORE_SNAIL,2,"Улитка", CardType.SHIP, CardFaction.BLOB,2,0,
                new SetEffects(List.of(new EffectDto(EffectType.COMBAT,4), new EffectDto(EffectType.EXILE,1)), SetEffectsType.AND),
                new SetEffects(List.of(new EffectDto(EffectType.COMBAT,2)), SetEffectsType.AND),
                null),

        new CardDefinition(CardCode.CORE_TRADE_CENTER,2,"Центр торговли", CardType.BASE, CardFaction.TRADE_FEDERATION,4,4,
                new SetEffects(List.of(new EffectDto(EffectType.HEALTH,2), new EffectDto(EffectType.GOLD,2)), SetEffectsType.OR),
                null,
                new SetEffects(List.of(new EffectDto(EffectType.COMBAT,5)), SetEffectsType.AND)));
    //TODO добавить карту Игла, Мехмир, Яхта, Аквариум
    }
