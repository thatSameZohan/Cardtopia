'use client';
import { useRouter } from 'next/navigation';
import React from 'react';
import { useGameRoom } from '../hook/useGameRoom';

export default function GameRoom({ roomId }: { roomId: string }) {
  const router = useRouter();
  const { messages, sendEvent, leaveRoom, roomInfo, deleteRoom } =
    useGameRoom(roomId);
  console.log(messages, 'messages', roomInfo);
  const waiting = roomInfo && roomInfo.participantsCount < 2;
  return (
    <>
      <h1>
        Game Room ID: {roomId} Нужно еще добавить loader пока игроки ждут{' '}
      </h1>
      {waiting && <p>Ожидаем второго игрока…</p>}
      {!waiting && <p>Игра начинается!</p>}
      <button
        onClick={() => {
          leaveRoom();
          // if (roomInfo.participantsCount === 1) {
          //   deleteRoom();
          // } 
          router.back();
        }}
      >
        Покинуть комнату
      </button>
    </>
  );
}
