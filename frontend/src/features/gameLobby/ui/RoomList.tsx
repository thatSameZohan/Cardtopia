'use client';
import clsx from 'clsx';
import React, { useEffect, useRef } from 'react';
import { useRouter } from 'next/navigation';
import { useRooms } from '../hook/useRooms';
import styles from './RoomList.module.scss';
import { Room } from '../type/type';

import { useSelector } from 'react-redux';
import { RootState } from '@/redux/store';
import { toast } from 'react-toastify';
import Cookies from 'js-cookie';

export default function RoomList() {
  const router = useRouter();
  const hasLeftRef = useRef(false);
  const username = useSelector((state: RootState) => state.auth.username);
  const { rooms, createRoom, joinRoom, connected, deleteRoom, leaveRoom } =
    useRooms();

  const handleJoinRoom = (roomId: string) => {
    const room = rooms.find((r) => r.id === roomId);

    if (!username) {
      toast.error('Пользователь не авторизован');
      return;
    }

    if (room?.players.includes(username)) {
      toast.error('Вы уже в этой комнате');
      return;
    }

    joinRoom(roomId);
    router.push(`/room/${roomId}`);
  };

  const handleCreateRoom = () => {
    createRoom();
  };

  useEffect(() => {
    if (!connected) return;
    if (!username) return;

    const roomInstanceId = Cookies.get('room_instance');
    if (!roomInstanceId) return;

    const room = rooms.find((r) => r.id === roomInstanceId);
    if (!room) return;

    if (!room.players.includes(username)) {
      Cookies.remove('room_instance');
      return;
    }

    if (hasLeftRef.current) return;
    hasLeftRef.current = true;

    leaveRoom(room.id);
    Cookies.remove('room_instance');
  }, [connected, rooms, username, leaveRoom]);
  return (
    <div className={styles.roomListContainer}>
      <div className={styles.header}>
        <h2 className={styles.title}>Доступные комнаты</h2>
        <button
          className={styles.createRoomButton}
          onClick={handleCreateRoom}
          disabled={!connected}
        >
          Создать комнату
        </button>
      </div>

      <ul className={styles.roomList}>
        {rooms.length > 0 ? (
          rooms.map((room: Room) => (
            <li
              key={room.id}
              className={clsx(styles.roomItem)}
              onClick={(e) => {
                e.preventDefault;
                handleJoinRoom(room.id);
              }}
            >
              <span className={styles.roomName}>{room.name}</span>
              <span className={styles.participantCount}>
                {room.participantsCount}/2
              </span>
              <p>
                участники: {room.players[0]} {room.players[1]}
              </p>
              <button onClick={() => deleteRoom(room.id)}>x</button>
            </li>
          ))
        ) : (
          <p className={styles.noRooms}>
            Нет доступных комнат. Создайте первую!
          </p>
        )}
      </ul>
    </div>
  );
}
