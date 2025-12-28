package org.spring.mapper;

import org.spring.dto.AbilityDto;
import org.spring.dto.CardDto;
import org.spring.enums.AbilityType;
import org.spring.model.CardEntity;
import org.springframework.stereotype.Component;
import org.spring.model.AbilityEntity;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class CardMapper {

    public CardDto fromEntity(CardEntity e) {
        List<AbilityDto> abilities = null;
        if (e.getAbilities() != null) {
            abilities = e.getAbilities().stream()
                    .map(this::abilityFromEntity)
                    .collect(Collectors.toList());
        }

        return new CardDto(
                UUID.randomUUID().toString().substring(0, 8),
                e.getName(),
                e.getType(),
                e.getFaction(),
                e.getCost(),
                e.getDefense(),
                abilities
        );
    }

    private AbilityDto abilityFromEntity(AbilityEntity e) {
        return new AbilityDto(
                e.getType() != null ? e.getType() : AbilityType.OTHER,
                e.getValue(),
                null,
                e.getCondition()
        );
    }
}
