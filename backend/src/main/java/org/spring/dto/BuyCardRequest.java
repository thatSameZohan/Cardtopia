package org.spring.dto;

import org.spring.domain.market.PurchaseType;

public record BuyCardRequest(String gameId, String marketCardId, PurchaseType type) {}
