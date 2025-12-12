import { useWSContext } from '@/shared/ui/WSProvider/WSProvider';
import { StompSubscription, IMessage } from '@stomp/stompjs';
import { useRef, useEffect, useState, useCallback } from 'react';

export interface Room {
  id: string;
  name: string;
  participantsCount: number;
  full: boolean;
}

export const useRooms = () => {
  const { connected, subscribe, publish } = useWSContext();
  const [rooms, setRooms] = useState<Room[]>([]);
  const roomsSub = useRef<StompSubscription | null>(null);
  const createdSub = useRef<StompSubscription | null>(null);
  const [newRoomId, setNewRoomId] = useState<string | null>(null);

  useEffect(() => {
    if (!connected) return;

    roomsSub.current = subscribe('/topic/rooms', (msg) => {
      const updated: Room[] = JSON.parse(msg.body);
      setRooms(updated.filter((room) => !room.full)); // только здесь обновляем список комнат
    });
    createdSub.current = subscribe(
      '/user/queue/rooms/created',
      (room: IMessage) => {
        const { id } = JSON.parse(room.body);
        setNewRoomId(id);
      },
    );

    publish('/app/rooms/list');

    return () => {
      roomsSub.current?.unsubscribe();
      createdSub.current?.unsubscribe();
    };
  }, [connected, subscribe, publish]);

  const createRoom = () => {
    publish('/app/rooms/add', 'Новая комната');
  };

  const joinRoom = async (roomId: string) => {
    return roomId;
  };
  const deleteRoom = async (roomId: string) =>
    publish('/app/rooms/delete', roomId);
  return { rooms, createRoom, joinRoom, connected, deleteRoom, newRoomId };
};
