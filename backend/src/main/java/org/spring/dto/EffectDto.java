package org.spring.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.spring.enums.EffectType;

@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
public class EffectDto {

    private EffectType type;

    private int value;

}