import React from 'react';
import { useSelector, useDispatch } from 'react-redux';
import { RootState } from '../../../redux/store';
import { sendAction, Player } from '../../../redux/gameSlice/gameSlice';
import { CardType } from '../../../features/cards/model/card.schema';

export const Hand = () => {
  const state = useSelector((s: RootState) => s.game);
  const dispatch = useDispatch();
  const player = state.players.find((p: Player) => p.id === state.activePlayerId);
  if(!player) return null;

  return (
    <div className="hand">
      {player.hand.map((c: CardType) => (
        <button key={c.id} onClick={() => dispatch(sendAction({ actionType: 'PLAY_CARD', payload: { cardId: c.id }}))}>
          {c.name} (A:{c.countAttack} G:0)
        </button>
      ))}
    </div>
  );
};