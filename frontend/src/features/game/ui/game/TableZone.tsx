'use client';

import { useDrop } from 'react-dnd';
import styles from './Game.module.scss';
import { CardType } from '../../type/type';
import { useRef, useState } from 'react';
import { Card } from './Card/Card';

type Props = {
  title: string;
  accept: string;
  cards?: CardType[];
  onDrop: (card: CardType) => void;
};

export function TableZone({ title, accept, cards = [], onDrop }: Props) {
  const ref = useRef<HTMLDivElement>(null);

  const [{ isOver }, drop] = useDrop(() => ({
    accept,
    drop: (item: CardType) => {
      onDrop(item);
    },
    collect: (monitor) => ({
      isOver: monitor.isOver(),
    }),
  }));

  drop(ref);

  return (
    <div
      ref={ref}
      className={styles.zone}
      style={{
        background: isOver ? '#d1ffd1' : '#f9f9f9',
        transition: '0.15s',
      }}
    >
      <h3>{title}</h3>

      {/* 🔥 РЕАЛЬНЫЙ РЕНДЕР КАРТ */}
      <div style={{ display: 'flex', gap: 10, flexWrap: 'wrap' }}>
        {cards.map((card) => (
          <Card key={card.id} {...card} variant="face" disabled />
        ))}
      </div>
    </div>
  );
}
