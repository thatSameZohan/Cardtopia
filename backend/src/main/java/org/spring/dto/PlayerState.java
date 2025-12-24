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
    private List<Card> deck = new LinkedList<>(); // колода
    private List<Card> discardPile = new ArrayList<>(); // сброс
    private List<Card> hand = new ArrayList<>(); // рука
    private List<Card> playedCards = new ArrayList<>(); // сыгранные карты
    private int currentAttack = 0;
    private int currentGold = 0;
    private int health = 20;

    public PlayerState(String playerId) {
        this.playerId = playerId;
    }
}