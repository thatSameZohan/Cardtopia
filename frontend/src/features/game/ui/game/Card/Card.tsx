'use client';
import { useRef } from 'react';
import clsx from 'clsx';
import styles from './Card.module.scss';
import { useDrag } from 'react-dnd';
import { CardType } from '@/features/game/type/type';

type CardProps = CardType & {
  disabled?: boolean;
  variant?: 'face' | 'back';
  dndType?: string;
  onClick?: () => void;
  draggable?: boolean;
};

export const Card = ({
  id,
  name,
  type,
  cost,
  defense,
  abilities = [],
  disabled,
  variant = 'face',
  dndType = 'card',
  draggable = true,
  onClick,
}: CardProps) => {
  const ref = useRef<HTMLDivElement>(null);

  const [{ isDragging }, drag] = useDrag(
    () => ({
      type: dndType,
      item: { id, name, type, cost, defense, abilities },
      canDrag: draggable && !disabled,
      collect: (monitor) => ({
        isDragging: monitor.isDragging(),
      }),
    }),
    [id, name, type, cost, defense, abilities, disabled, dndType],
  );

  drag(ref);

  const typeClass =
    type === 'Ship'
      ? styles.ship
      : type === 'Base'
        ? styles.base
        : styles.outpost;

  const handleClick = (e: React.MouseEvent) => {
    if (!isDragging && onClick) {
      onClick();
    }
  };

  return (
    <div
      ref={ref}
      className={clsx(
        styles.card,
        styles[variant],
        typeClass,
        disabled && styles.disabled,
      )}
      style={{
        cursor: disabled ? 'not-allowed' : 'grab',
      }}
      onClick={handleClick}
    >
      {variant === 'face' && (
        <div className={styles.container}>
          {cost !== undefined && cost > 0 && (
            <div className={styles.cost}>{cost}</div>
          )}
          {defense !== undefined && defense > 0 && (
            <div className={styles.content}>
              <div className={styles.defense}>DEF: {defense}</div>
            </div>
          )}
          <div className={styles.cardName}>{name}</div>
          {abilities.length > 0 && (
            <div className={styles.abilities}>
              {abilities.map((a, i) => (
                <div key={i} className={styles.ability}>
                  {a.type}: {a.value}
                </div>
              ))}
            </div>
          )}
        </div>
      )}
    </div>
  );
};
