'use client';

import React from 'react';
import { Card } from './Card/Card';
import { DiscardPile } from './DiscardPile';
import { Market } from './Market';

export const GameTable = ({ state, username, onPlayCard }: any) => {
  const me = state.players.find((p: any) => p.username === username);
  const opponent = state.players.find((p: any) => p.username !== username);

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: 20 }}>
      {/* Карты оппонента */}
      <div>
        <h3>Оппонент</h3>
        <div style={{ display: 'flex', gap: 5 }}>
          {opponent.hand.map((card: any) => (
            <Card key={card.id} {...card} disabled />
          ))}
        </div>
      </div>

      {/* Игровой стол */}
      <div>
        <h3>Стол</h3>
        <div style={{ display: 'flex', gap: 5 }}>
          {state.table.map((card: any) => (
            <Card key={card.id} {...card} />
          ))}
        </div>
      </div>

      {/* Ваши карты */}
      <div>
        <h3>Ваши карты</h3>
        <div style={{ display: 'flex', gap: 5 }}>
          {me.hand.map((card: any) => (
            <Card key={card.id} {...card} onClick={() => onPlayCard(card.id)} />
          ))}
        </div>
      </div>

      {/* Рынок */}
      <Market cards={state.market} onBuy={onPlayCard} />

      {/* Сброс */}
      <DiscardPile cards={state.discard} />
    </div>
  );
};
