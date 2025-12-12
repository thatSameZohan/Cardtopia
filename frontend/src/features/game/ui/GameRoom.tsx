'use client';
import { useRouter } from 'next/navigation';
import React, { useEffect } from 'react';
import { useGameRoom } from '../hook/useGameRoom';

export default function GameRoom({ roomId }: { roomId: string }) {
  const router = useRouter();
  const { messages, sendEvent, leaveRoom, roomInfo, deleteRoom } =
    useGameRoom(roomId);
  const waiting = roomInfo && roomInfo?.participantsCount < 2;
  return roomInfo && roomInfo.participantsCount > 2 ? (
    <div>
      <p>Комната полна. Вы не можете присоединиться.</p>
      <button onClick={() => router.back()}>Вернуться назад</button>
    </div>
  ) : (
    <>
      <h1>Game Room ID: {roomId} Нужно еще добавить loader пока игроки ждут</h1>
      {waiting && <p>Ожидаем второго игрока…</p>}
      {!waiting && <p>Игра начинается!</p>}
      <button
        onClick={() => {
          leaveRoom();
          router.back();
        }}
      >
        Покинуть комнату
      </button>
    </>
  );
}
