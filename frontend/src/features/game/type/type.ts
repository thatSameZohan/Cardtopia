export type GameMessage<T = unknown> = {
  type: string;
  payload: T;
};

export type Player = {
  hand: CardType[];
  playedCards: CardType[];
};

export type PlayersMap = Record<string, PlayerState>;

export type GameStatus = 'WAITING_FOR_PLAYER' | 'IN_PROGRESS' | 'FINISHED';
export interface GameState {
  gameId: string;
  activePlayerId: string;
  status: GameStatus;
  winnerId: string | null;
  market: CardType[];
  players: PlayersMap;
}

export interface PlayerState {
  playerId: string;
  active: boolean;
  health: number;
  currentAttack: number;
  currentGold: number;
  deckSize: number;
  discardSize: number;
  handSize: number;
}
// type CardAbility = null;

// export interface CardType {
//   id: string;
//   attack?: number;
//   gold?: number;
//   cost?: number;
//   type?: 'card' | 'market';
//   ability?: CardAbility | null;
// }

export type MarketProps = {
  cards: CardType[];
  gold: number;
  onBuy: (cardId: string, cardCost: number) => void;
};
export type CardTypeName = 'SHIP' | 'BASE' | 'OUTPOST';

export interface CardType {
  id: string;
  name?: string;
  type: CardTypeName;
  faction?: string;
  defense?: number;
  cost?: number;
  abilities?: Ability[];
}
export type Ability = {
  type: 'TRADE' | 'COMBAT' | 'ACQUIRE' | 'SCRAP';
  value: number;
  trigger?: string | null;
  condition?: string | null;
};
