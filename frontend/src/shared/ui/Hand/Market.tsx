import React from 'react';
import { useSelector, useDispatch } from 'react-redux';
import { RootState } from '../../../redux/store';
import { sendAction } from '../../../redux/gameSlice/gameSlice';
import { CardType } from '../../../features/cards/model/card.schema';

export const Market = () => {
  const state = useSelector((s: RootState) => s.game);
  const dispatch = useDispatch();

  return (
    <div className="market">
      {state.market.map((c: CardType) => (
        <button key={c.id} onClick={() => dispatch(sendAction({ actionType: 'BUY_CARD', payload: { cardId: c.id }}))}>
          {c.name} (Cost: 0)
        </button>
      ))}
    </div>
  );
};
