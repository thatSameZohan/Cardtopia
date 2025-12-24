'use client';

import React from 'react';
import { Card } from './Card/Card';
import { MarketProps } from '../../type/type';

export const Market = ({ cards, onBuy }: MarketProps) => (
  <div>
    <h3 style={{ color: 'white' }}>Рынок</h3>
    <div style={{ display: 'flex', gap: 5 }}>
      {cards.map((card: any) => (
        <Card
          type="market"
          key={card.id}
          {...card}
          onClick={() => onBuy(card.id, card.cost)}
        />
      ))}
    </div>
  </div>
);
