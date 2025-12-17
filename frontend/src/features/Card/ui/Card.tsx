import React from 'react';
import styles from './Card.module.scss';
import { CardType } from '..';

type CardProps = {
  card: CardType;
};

export const Card = ({ card }: CardProps) => {
  const { name, attack, cost } = card;

  return (
    <div className={styles.wrapper}>
      <h3>{name}</h3>
      <p>Цена: {cost}</p>
      <p>Атака: {attack}</p>
    </div>
  );
};
