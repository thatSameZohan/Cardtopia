'use client';

import React from 'react';
import { Card } from './Card/Card';

export const Market = ({ cards, onBuy }: any) => (
  <div>
    <h3>Рынок</h3>
    <div style={{ display: 'flex', gap: 5 }}>
      {cards.map((card: any) => (
        <Card key={card.id} {...card} onClick={() => onBuy(card.id)} />
      ))}
    </div>
  </div>
);
