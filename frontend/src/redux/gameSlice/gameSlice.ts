import { createSlice, PayloadAction, createAction } from '@reduxjs/toolkit';
import { CardType } from '@/features/cards/model/card.schema';

export interface Player { id:string; health:number; gold:number; hand:CardType[]; deck:CardType[]; discard:CardType[]; played:CardType[]; }

export interface GameState {
  roomId: string;
  activePlayerId: string;
  players: Player[];
  market: CardType[];
}

const initialState: GameState = {
  roomId: '',
  activePlayerId: '',
  players: [],
  market: []
};

// Action creators for middleware
export const connect = createAction<{ roomId: string; playerId: string }>('game/connect');
export const disconnect = createAction('game/disconnect');
export const sendAction = createAction<{ actionType: string; payload: any }>('game/sendAction');

const gameSlice = createSlice({
  name: 'game',
  initialState,
  reducers: {
    setState: (state, action: PayloadAction<GameState>) => action.payload,
    resetGame: () => initialState
  }
});

export const { setState, resetGame } = gameSlice.actions;
export default gameSlice.reducer;
