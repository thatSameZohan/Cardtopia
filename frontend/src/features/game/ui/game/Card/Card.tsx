'use client';

import React from 'react';

type CardProps = {
  id: string;
  name: string;
  attack: number;
  cost: number;
  onClick?: () => void;
  disabled?: boolean;
};

export const Card = ({ name, attack, cost, onClick, disabled }: CardProps) => {
  return (
    <div
      onClick={disabled ? undefined : onClick}
      style={{
        border: '1px solid black',
        padding: 5,
        width: 80,
        cursor: disabled ? 'not-allowed' : 'pointer',
        opacity: disabled ? 0.5 : 1,
        borderRadius: 8,
        backgroundColor: '#fffbe6',
      }}
    >
      <h4>{name}</h4>
      <p>Атака: {attack}</p>
      <p>Стоимость: {cost}</p>
    </div>
  );
};
