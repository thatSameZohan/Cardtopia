export type GameMessage = {
  type: string;
  payload: any;
};

export type CardType = {
  name: string;
  attack?: number | null;
  cost?: number | null;
};
