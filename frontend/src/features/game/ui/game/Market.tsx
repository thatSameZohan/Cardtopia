'use client';

import React from 'react';
import { Card } from './Card/Card';
import { MarketProps } from '../../type/type';
import clsx from 'clsx';
import styles from './Card/Card.module.scss';

export const Market = ({ cards, onBuy, gold }: MarketProps) => (
  <div>
    <h3 style={{ color: 'white' }}>Рынок</h3>
    <div style={{ display: 'flex', gap: 5 }}>
      {cards.map((card: any) => {
        const canBuy = gold >= card.cost;
        return (
          <Card
            type="market"
            key={card.id}
            {...card}
            disabled={!canBuy} // чтобы нельзя было перетаскивать если нельзя купить
            className={clsx(canBuy && styles.highlighted)} // добавляем подсветку
            onClick={() => canBuy && onBuy(card.id, card.cost)}
          />
        );
      })}
    </div>
  </div>
);
