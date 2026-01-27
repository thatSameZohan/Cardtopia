package org.spring.dto;

import org.spring.enums.Faction;

public record PlayCardRequest(String gameId, String cardId, boolean scrap, Faction faction) {
}
