package org.spring.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.spring.enums.SetEffectsType;

import java.util.List;
@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
public class SetEffects {

    private List<EffectDto> effects;
    private SetEffectsType type;
}
