import React from 'react';
import { Card } from './Card';
import { CardType } from '@/features/game/type/type';

export const OutpostCard = (props: CardType) => (
  <Card {...props} className="outpost" showDefense isOutpost />
);
