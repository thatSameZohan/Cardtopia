import { CardType } from '../model/card.schema';

export const drawRandomCards = (cards: CardType[], count: number) => {
  const available = cards.filter((c) => !c.used);
  if (!available.length) return [];
  const shuffled = [...available].sort(() => Math.random() - 0.5);
  return shuffled.slice(0, count);
};

export const markCardsUsed = (initialCards: CardType[], cardsToMark: CardType[]) =>
  initialCards.map((c) => (cardsToMark.find((d) => d.id === c.id) ? { ...c, used: true } : c));
