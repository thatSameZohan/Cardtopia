'use client';
import React, { useState, useEffect } from 'react';
import { useDrop } from 'react-dnd';
import styles from './Game.module.scss';
import { Card } from './Card/Card';
import { CardType } from '../../type/type';

type TableZoneProps = {
  title: string;
  accept: CardType['type']; // 'card' | 'market'
  onDrop: (card: CardType) => void;
  initialCards?: CardType[];
  onClear?: React.RefObject<{ clear: () => void } | null>;
};

export function TableZone({
  title,
  accept,
  onDrop,
  initialCards = [],
  onClear,
}: TableZoneProps) {
  const [cards, setCards] = useState<CardType[]>(initialCards);

  const [{ isOver }, drop] = useDrop<CardType, void, { isOver: boolean }>(
    () => ({
      accept,
      drop: (item) => {
        if (!cards.find((c) => c.id === item.id)) {
          setCards((prev) => [...prev, item]);
          onDrop(item);
        }
      },
      collect: (monitor) => ({
        isOver: monitor.isOver(),
      }),
    }),
    [cards, onDrop, accept],
  );

  useEffect(() => {
    if (onClear) {
      onClear.current = { clear: () => setCards([]) };
    }
  }, [onClear]);

  return (
    <div
      ref={drop as unknown as React.Ref<HTMLDivElement>}
      className={styles.zone}
      style={{ background: isOver ? '#e0ffe0' : '#f9f9f9' }}
    >
      <h3>{title}</h3>
      {cards.map((card) => (
        <Card key={card.id} {...card} variant="face" disabled />
      ))}
    </div>
  );
}
