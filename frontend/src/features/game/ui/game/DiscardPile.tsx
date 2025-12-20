'use client';

import React from 'react';
import { Card } from '../..';


export const DiscardPile = ({ cards }: any) => (
  <div>
    <h3>Сброс</h3>
    <div style={{ display: 'flex', gap: 5 }}>
      {cards.map((card: any) => (
        <Card key={card.id} {...card} disabled />
      ))}
    </div>
  </div>
);
