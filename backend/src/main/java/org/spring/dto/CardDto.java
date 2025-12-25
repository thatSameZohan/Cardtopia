package org.spring.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@AllArgsConstructor
@Getter
@Setter
@ToString
public class CardDto {

    private final String id;
    private String name;
    private String type;
    private String faction;
    private final int cost;
    private int defense;
    private List<AbilityDto> abilities;
}