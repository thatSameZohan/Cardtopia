package org.spring.dto;

import org.spring.domain.card.CardFaction;

public record PlayCardRequest(String gameId, String cardId, boolean scrap, CardFaction cardFaction) {
}
