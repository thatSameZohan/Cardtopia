import { useDrag } from 'react-dnd';
import styles from './Card.module.scss';
import { useEffect } from 'react';
import { CardType } from '../model/card.schema';

type CardProps = {
  card: CardType;
  onDragStart?: (card: CardType) => void;
  onDragEnd?: (card: CardType, didDrop: boolean) => void;
};

export function Card({ card, onDragStart, onDragEnd }: CardProps) {
  const [{ isDragging }, drag] = useDrag(
    () => ({
      type: 'card',
      item: () => card,
      end: (item: CardType | undefined, monitor) => {
        const didDrop = monitor.didDrop();
        if (onDragEnd && item) onDragEnd(item, didDrop);
      },
      collect: (monitor) => ({ isDragging: !!monitor.isDragging() }),
    }),
    [card, onDragEnd],
  );

  useEffect(() => {
    if (isDragging) onDragStart?.(card);
  }, [isDragging, card, onDragStart]);

  return (
    <div
      ref={drag as any}
      className={styles.card}
      style={{
        opacity: isDragging ? 0.5 : 1,
      }}
    >
      <h1 className={styles.countAttack}> Атака: {card.countAttack}</h1>
      <h3 className={styles.name}>{card.name}</h3>
    </div>
  );
}
