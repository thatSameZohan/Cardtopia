import React from 'react';
import { Card } from './Card';
import { CardType } from '@/features/game/type/type';

export const ShipCard = (props: CardType) => (
  <Card {...props} className="ship" />
);