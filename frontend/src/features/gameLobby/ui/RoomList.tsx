'use client';
import clsx from 'clsx';
import { useRouter } from 'next/navigation';
import { useRooms } from '../hook/useRooms';
import styles from './RoomList.module.scss';
import { useEffect } from 'react';
import { RootState, useAppSelector } from '@/redux/store';

export default function RoomList() {
  const router = useRouter();

  const {
    rooms,
    createRoom,
    joinRoom,
    connected,
    deleteRoom,
    newRoomId,
    leaveRoom,
  } = useRooms();

  const handleJoinRoom = async (roomId: string) => {
    joinRoom(roomId);
    console.log('joinRoom');
    // router.push(`/room/${id}`);
  };
  const handleCreateRoom = () => {
    createRoom();
  };
  // useEffect(() => {
  //   if (newRoomId) {
  //     router.push(`/room/${newRoomId}`);
  //   }
  // }, [newRoomId]);
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
          rooms.map((room) => (
            <li
              key={room.id}
              className={clsx(styles.roomItem, room.full && styles.block)}
              // onClick={() => handleJoinRoom(room.id)}
            >
              <span className={styles.roomName}>{room.name}</span>
              <span className={styles.participantCount}>
                {room.participantsCount}/2
              </span>
              <button onClick={() => handleJoinRoom(room.id)}>join</button>
              <button onClick={() => leaveRoom(room.id)}>leave</button>
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
