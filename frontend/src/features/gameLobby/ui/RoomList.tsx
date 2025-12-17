'use client';
import clsx from 'clsx';
import { useRouter } from 'next/navigation';
import { useRooms } from '../hook/useRooms';
import styles from './RoomList.module.scss';
import { Room } from '../type/type';

export default function RoomList() {
  const router = useRouter();

  const { rooms, createRoom, joinRoom, connected, deleteRoom, leaveRoom } =
    useRooms();

  const handleJoinRoom = async (roomId: string) => {
    joinRoom(roomId);
    router.push(`/room/${roomId}`);
  };
  const handleCreateRoom = () => {
    createRoom();
  };
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
              className={clsx(styles.roomItem, room.isFull && styles.block)}
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
