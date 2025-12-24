'use client';

import { useRouter } from 'next/navigation';
import React, { useEffect, useCallback, useRef } from 'react';
import Cookies from 'js-cookie';

import { useRooms } from '@/features/gameLobby/hook/useRooms';
import { routes } from '@/shared/router/paths';
import { useSelector } from 'react-redux';
import { RootState } from '@/redux/store';
import { RoomHeader } from './RoomHeader';
import { RoomStatus } from './RoomStatus';
import { useWSContext } from '@/shared/ui/WSProvider/WSProvider';

type Props = {
  roomId: string;
};

const COOKIE_KEY = 'room_instance';

export function Room({ roomId }: Props) {
  const router = useRouter();
  const { leaveRoom, connected, rooms } = useRooms(roomId);
  const { publish } = useWSContext();
  const username = useSelector((state: RootState) => state.auth.username);

  const room = rooms.find((r) => r.id === roomId);

  const waitingForSecondPlayer = room?.participantsCount === 1;
  const isUserInRoom = !!(room && username && room.players.includes(username));

  //Защита от повторного leave
  const isLeavingRef = useRef(false);

  const safeLeave = useCallback(() => {
    if (isLeavingRef.current) return;
    isLeavingRef.current = true;
    leaveRoom(roomId);
  }, [leaveRoom, roomId]);

  const leaveAndExit = useCallback(() => {
    safeLeave();
    Cookies.remove(COOKIE_KEY);
    router.replace(routes.homepage);
  }, [safeLeave, router]);

  useEffect(() => {
    Cookies.set(COOKIE_KEY, roomId);
  }, [roomId]);

  useEffect(() => {
    if (room && username && !room.players.includes(username)) {
      Cookies.remove(COOKIE_KEY);
      router.replace(routes.homepage);
    }
  }, [room, username, router]);

  useEffect(() => {
    const onPopState = () => {
      safeLeave();
      Cookies.remove(COOKIE_KEY);
    };

    window.addEventListener('popstate', onPopState);
    return () => window.removeEventListener('popstate', onPopState);
  }, [safeLeave]);
  return (
    <div>
      <RoomHeader
        roomId={roomId}
        connected={connected}
        username={username ?? ''}
      />
      <RoomStatus
        connected={connected}
        waiting={waitingForSecondPlayer}
        isUserInRoom={isUserInRoom}
      />
      <div style={{ margin: '10px 0' }}>
        {room?.creatorName === username && (
          <button
            onClick={() => {
              publish('/app/game.start', JSON.stringify({ roomId }));
            }}
          >
            Начать игру
          </button>
        )}
      </div>

      <button onClick={leaveAndExit}>Покинуть комнату</button>
    </div>
  );
}
