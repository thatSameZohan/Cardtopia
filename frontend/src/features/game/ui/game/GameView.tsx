'use client';

import React from 'react';
import { useGame } from '../../hook/useGame';
import { GameTable } from './GameTable';
import { usePrivateGame } from '../..';
import { Room } from '@/features/gameLobby/type/type';

type PropsRoom = {
  room: Room;
};

export const GameView = ({ room }: PropsRoom) => {
  const { messages } = useGame(room);
  console.log(messages);
  // const playerPrivate = usePrivateGame();

  //   if (!state) return <p>Загрузка игры…</p>;

  //   const handlePlayCard = (cardId: string, target?: string) => {
  //     sendAction({
  //       type: 'PLAY_CARD',
  //       payload: { cardId, target },
  //     });
  //   };

  //   const handleEndTurn = () => {
  //     sendAction({ type: 'END_TURN' });
  //   };

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: 10 }}>
      <h2>Игра: {room.id}</h2>
      {/* <p>Ваш ход: {isMyTurn ? 'Да' : 'Нет'}</p>

      <GameTable
        state={state}
        username={username}
        onPlayCard={handlePlayCard}
      />

      {isMyTurn && <button onClick={handleEndTurn}>Завершить ход</button>} */}
    </div>
  );
};
