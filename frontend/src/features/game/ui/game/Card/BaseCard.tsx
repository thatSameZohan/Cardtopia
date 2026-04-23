import React from 'react';
import { Card } from './Card';
import { CardType } from '@/features/game/type/type';

export const BaseCard = (props: CardType) => (
  <Card {...props} className="base" showDefense />
);
