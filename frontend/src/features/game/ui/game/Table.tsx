'use client';

import { useState, useRef } from 'react';
import { Card } from './Card/Card';
import { Market } from './Market';
import { CardType, GameState, Player } from '../../type/type';
import styles from './Game.module.scss';
import clsx from 'clsx';
import { TableZone } from '../..';
import { ShipCard } from './Card/ShipCard';
import { BaseCard } from './Card/BaseCard';
import { OutpostCard } from './Card/OutpostCard';

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

  const [tableCards, setTableCards] = useState<CardType[]>([]);

  const tableRef = useRef<{ clear: () => void }>(null);

  const handlePlayCard = (card: CardType) => {
    const trade = card.abilities?.find((a) => a.type === 'TRADE');
    const combat = card.abilities?.find((a) => a.type === 'COMBAT');

    setGold((p) => p + (trade?.value ?? 0));
    setAttack((p) => p + (combat?.value ?? 0));

    setTableCards((prev) => [...prev, card]);

    onPlayCard(card.id);
  };

  const handleBuyCard = (cardId: string, cardCost: number) => {
    if (cardCost > gold) return;
    setGold((p) => p - cardCost);
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
    setTableCards([]);
    tableRef.current?.clear?.();
  };

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: 20 }}>
      {/* ОППОНЕНТ */}
      <div>
        <h3 className={styles.opponent__title}>
          Оппонент: {opponent?.playerId}
        </h3>
        <h1 className={styles.opponent__title}>❤ {opponent?.health}</h1>

        <div className={styles['opponent__card-container']}>
          {Array.from({ length: opponent?.handSize ?? 0 }).map((_, i) => (
            <Card
              key={i}
              id={`opponent-${i}`}
              variant="back"
              type="SHIP"
              dndType="card"
            />
          ))}
        </div>
      </div>

      {/* РЫНОК */}
      <Market
        gold={gold}
        cards={stateGame.market}
        onBuy={(id, cost) => handleBuyCard(id, cost)}
      />

      {/* СТАТУС */}
      <div>
        <h3>Стол</h3>
        <h3 className={styles.me__title}>Золото: {gold}</h3>
        <h3 className={styles.me__title}>Атака: {attack}</h3>
      </div>

      {/* СТОЛ */}
      <TableZone
        title="Стол"
        accept="card"
        cards={tableCards}
        onDrop={handlePlayCard}
        // onClear={tableRef}
      />

      {/* РУКА */}
      <div className={clsx(stateGame.activePlayerId === username && styles.me)}>
        <h3 className={styles.me__title}>Ваши карты</h3>
        <h3 className={styles.me__title}>{me?.playerId}</h3>
        <h1 className={styles.me__title}>❤ {me?.health}</h1>

        <div className={styles['me__card-container']}>
          {player?.hand.map((card) => {
            switch (card.type) {
              case 'SHIP':
                return <ShipCard key={card.id} {...card} />;
              case 'BASE':
                return <BaseCard key={card.id} {...card} />;
              case 'OUTPOST':
                return <OutpostCard key={card.id} {...card} />;
              default:
                return null;
            }
          })}
        </div>
      </div>

      {/* КНОПКИ */}
      <button onClick={handleAttack}>Attack</button>
      <button onClick={handleEndTurn}>End Turn</button>
    </div>
  );
};
