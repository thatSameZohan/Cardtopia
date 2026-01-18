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
    private int currentAttack = 0;
    private int currentGold = 0;
    private int health = 20;
    private List<CardInstance> deck = new LinkedList<>(); // колода игрока
    private List<CardInstance> discardPile = new ArrayList<>(); // стопка сброса
    private List<CardInstance> hand = new ArrayList<>(); // рука
    private List<CardInstance> playedCards = new ArrayList<>(); // сыгранные карты
    private List<CardInstance> bases = new ArrayList<>(); // базы игрока
    private List<CardInstance> outposts = new ArrayList<>(); // аванпосты игрока

    public PlayerState(String playerId) {
        this.playerId = playerId;
    }
}