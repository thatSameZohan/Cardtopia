package org.spring.dto;

public record BuyCardRequest(String gameId, String marketCardId, boolean topDeck) {}
