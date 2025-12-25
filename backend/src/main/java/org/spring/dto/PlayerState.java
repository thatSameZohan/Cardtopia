package org.spring.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Getter
@Setter
@ToString
public class PlayerState {
    private final String playerId;
    private List<CardDto> deck = new LinkedList<>(); // колода
    private List<CardDto> discardPile = new ArrayList<>(); // сброс
    private List<CardDto> hand = new ArrayList<>(); // рука
    private List<CardDto> playedCard = new ArrayList<>(); // сыгранные карты
    private int currentAttack = 0;
    private int currentGold = 0;
    private int health = 20;

    public PlayerState(String playerId) {
        this.playerId = playerId;
    }
}