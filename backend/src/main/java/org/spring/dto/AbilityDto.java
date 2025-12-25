package org.spring.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.spring.enums.AbilityType;

@AllArgsConstructor
@Getter
@Setter
public class AbilityDto {

    private AbilityType type;        // Тип способности: COMBAT, TRADE, DRAW, SCRAP, ALLY и т.д.
    private int value;               // Значение способности
    private String trigger;          // Триггер: PLAY, PURCHASE, ATTACK и т.д.
    private String condition;        // Условие применения
}