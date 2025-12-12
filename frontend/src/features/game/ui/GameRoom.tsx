'use client';

import { useRouter } from 'next/navigation';
import React, { useEffect } from 'react';
import { useGameRoom } from '../hook/useGameRoom';
import { useWSContext } from '@/shared/ui/WSProvider/WSProvider';

interface GameRoomProps {
  roomId: string;
}

export default function GameRoom({ roomId }: GameRoomProps) {
  const router = useRouter();
  const { publish } = useWSContext();
  const {
    messages,
    leaveRoom,
    roomInfo,
    endTurn,
    turn,
    myParticipantId,
    isMyTurn,
  } = useGameRoom(roomId);

  const waitingForSecondPlayer = roomInfo && roomInfo?.participantsCount < 2;

  useEffect(() => {
    if (roomInfo?.participantsCount === 2) {
      // Начало игры, когда оба игрока подключены
      publish(`/app/room/${roomId}/start`);
    }
  }, [roomInfo?.participantsCount, roomId]);

  if (roomInfo && roomInfo?.participantsCount > 2) {
    return (
      <div>
        <p>Комната полна. Вы не можете присоединиться.</p>
        <button onClick={() => router.back()}>Вернуться назад</button>
      </div>
    );
  }

  return (
    <div>
      <h1>Game Room ID: {roomId}</h1>
      {waitingForSecondPlayer ? (
        <p>Ожидаем второго игрока…</p>
      ) : (
        <p>Игра начинается!</p>
      )}
      <h2>Ходит: {turn}</h2>
      <button disabled={!isMyTurn} onClick={endTurn}>
        Сделать ход
      </button>
      <button
        onClick={() => {
          leaveRoom();
          router.back();
        }}
      >
        Покинуть комнату
      </button>
      <div>
        <h3>Сообщения:</h3>
        <ul>
          {messages.map((msg, idx) => (
            <li key={idx}>
              {msg.type}: {JSON.stringify(msg.payload)}
            </li>
          ))}
        </ul>
      </div>
    </div>
  );
}
