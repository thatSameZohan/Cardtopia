export type GameMessage = {
  type: string;
  payload: any;
};

export type CardType = {
  id: string;
  attack?: number;
  cost?: number;
  gold?: number;
  type: 'card' | 'market';
};

export type Player = {
  playerId: string;
  health: number;
  currentAttack: number;
  currentGold: number;
  hand: CardType[];
};

export type PlayersMap = Record<string, Player>;

export type GameState = {
  id: string;
  status: 'IN_PROGRESS' | 'FINISHED' | 'WAITING';
  activePlayerId: string;
  players: PlayersMap;
  market: CardType[];
  marketDeck: CardType[];
};
export type MarketProps = {
  cards: CardType[];
  onBuy: (cardId: string, cardCost: number) => void;
};
