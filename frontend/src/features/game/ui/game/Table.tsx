'use client';
import React, { useState, useRef } from 'react';
import { Card } from './Card/Card';
import { Market } from './Market';
import { GameState, Player } from '../../type/type';
import styles from './Game.module.scss';
import clsx from 'clsx';
import { TableZone } from '../..';

type Props = {
  player: Player | null;
  stateGame: GameState;
  username: string | null;
  onPlayCard: (cardId: string) => void;
  onBuyCard: (cardId: string) => void;
  onAttack: () => void;
  onEndTurn: () => void;
};

export const Table = ({
  player,
  stateGame,
  username,
  onPlayCard,
  onBuyCard,
  onAttack,
  onEndTurn,
}: Props) => {
  const me = Object.values(stateGame.players).find(
    (p) => p.playerId === username,
  );
  const opponent = Object.values(stateGame.players).find(
    (p) => p.playerId !== username,
  );

  const [gold, setGold] = useState(0);
  const [attack, setAttack] = useState(0);

  const tableRef = useRef<{ clear: () => void }>(null);

  const handlePlayCard = (
    cardId: string,
    cardCost: number,
    cardAttack: number,
  ) => {
    setGold((prev) => prev + cardCost);
    setAttack((prev) => prev + cardAttack);

    onPlayCard(cardId); // вызывается корректно
  };

  const handleBuyCard = (cardId: string, cardCost: number) => {
    if (cardCost > gold) return;
    setGold((prev) => prev - cardCost);
    onBuyCard(cardId);
  };

  const handleAttack = () => {
    onAttack();
    setAttack(0);
  };

  const handleEndTurn = () => {
    onEndTurn();
    setGold(0);
    setAttack(0);
    tableRef.current?.clear(); // очищаем стол
  };

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: 20 }}>
      {/* Оппонент */}
      <div>
        <h3 className={styles['opponent__title']}>
          Оппонент: {opponent?.playerId}
        </h3>
        <h1 className={styles['opponent__title']}>❤ {opponent?.health}</h1>
        <div className={styles['opponent__card-container']}>
          {Array.from({ length: opponent?.handSize ?? 0 }).map((_, index) => (
            <Card key={index} id={`opponent-card-${index}`} variant="back" />
          ))}
        </div>
      </div>
      {/* Рынок */}
      <Market
        gold={gold}
        cards={stateGame.market}
        onBuy={(cardId, cardCost) => handleBuyCard(cardId, cardCost)}
      />
      {/* Стол */}
      <div>
        <h3>Стол</h3>
        <h3 className={styles['me__title']}>Золото: {gold}</h3>
        <h3 className={styles['me__title']}>Атака: {attack}</h3>
      </div>
      <TableZone
        title="Стол"
        accept="card"
        onDrop={(card) =>
          handlePlayCard(card.id, card.gold ?? 0, card.attack ?? 0)
        }
        onClear={tableRef}
      />
      {/* <TableZone
        title="Рынок"
        accept="market"
        onDrop={(card) => handleBuyCard(card.id, card.cost ?? 0)}
      /> */}
      {/* Ваши карты */}
      <div className={clsx(stateGame.activePlayerId === username && styles.me)}>
        <h3 className={styles['me__title']}>Ваши карты</h3>
        <h3 className={styles['me__title']}>{me?.playerId}</h3>
        <h1 className={styles['me__title']}>❤ {me?.health}</h1>
        <div className={styles['me__card-container']}>
          {player?.hand.map((card) => (
            <Card
              type="card"
              key={card.id}
              id={card.id}
              attack={card.attack}
              cost={card.cost}
              gold={card.gold}
            />
          ))}
        </div>
      </div>
      {/* Кнопки */}
      <button onClick={handleAttack}>Attack</button>
      <button onClick={handleEndTurn}>End Turn</button>
    </div>
  );
};
