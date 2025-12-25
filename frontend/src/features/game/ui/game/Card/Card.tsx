'use client';
import React, { useRef } from 'react';
import clsx from 'clsx';
import styles from './Card.module.scss';
import { useDrag } from 'react-dnd';
import { CardType } from '@/features/game/type/type';

type CardProps = CardType & {
  disabled?: boolean;
  variant?: 'face' | 'back';
};

export const Card = ({
  id,
  attack,
  cost,
  gold,
  disabled,
  variant = 'face',
  type = 'card',
}: CardProps) => {
  const ref = useRef<HTMLDivElement>(null);

  const [, drag] = useDrag(
    () => ({
      type,
      item: { id, attack, cost, gold, type } as CardType,
      canDrag: !disabled,
    }),
    [id, attack, cost, gold, disabled, type],
  );

  drag(ref); // навязываем drag на элемент

  return (
    <div
      ref={ref}
      style={{ cursor: disabled ? 'not-allowed' : 'grab' }}
      className={clsx(
        styles.card,
        styles[variant],
        disabled && styles.disabled,
      )}
    >
      {variant === 'face' && (
        <>
          {cost !== undefined && <div className={styles.cost}>💰 {cost}</div>}
          {gold !== undefined && (
            <div className={styles.gold}>🪙 {gold}!!!</div>
          )}
          {attack !== undefined && (
            <div className={styles.attack}>⚔ {attack}</div>
          )}
        </>
      )}
    </div>
  );
};
