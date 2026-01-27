package org.spring.enums;

public enum EffectType {
    COMBAT,
    GOLD,
    DRAW,
    HEALTH,
    EXILE, // право удалить карту из руки или сброса
    FORCE_DISCARD, //противник сбрасывает карту
    DISCARD_DRAW, // сбрось до n карт, а затем возьми столько же карт
    DESTROY_BASE, // уничтожить базу противника
    SELECT_FACTION, // выбор фракции для карты Наемник
    TOP_DECK_NEXT_SHIP,// сколько следующих купленных кораблей можно положить на верх колоды
    INCREASE_COMBAT, // увеличить атаку всех кораблей в руке
    DRAW_CONDITIONAL_BASE, // КОСТЫЛЬ если у тебя в игре не менее N баз, возьми N карт
    EXILE_DRAW,  // КОСТЫЛЬ Удали из игры до n карт своей руки и/или своего сброса. Возьми столько же карт
    BUY_FREE_TOP_DECK,
    EMPTY
}