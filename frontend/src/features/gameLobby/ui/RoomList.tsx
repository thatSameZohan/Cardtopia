'use client';

import { useRouter } from 'next/navigation';
import { useRooms } from '../hook/useRooms';
import styles from './RoomList.module.scss';

export default function RoomList() {
  const router = useRouter();
  const { rooms, createRoom, joinRoom, connected, deleteRoom } = useRooms();

  const handleJoinRoom = async (roomId: string) => {
    const id = await joinRoom(roomId);
    router.push(`/room/${id}`);
  };
  const handleCreateRoom = async () => {
    const id = await createRoom();
    router.push(`/room/${id}`);
  };
  console.log(rooms);

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
              className={styles.roomItem}
              onClick={() => handleJoinRoom(room.id)}
            >
              <span className={styles.roomName}>{room.name}</span>
              <span className={styles.participantCount}>
                {room.participantsCount}/2
              </span>
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
