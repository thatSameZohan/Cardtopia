import { useWSContext } from '@/shared/ui/WSProvider/WSProvider';
import { StompSubscription, IMessage } from '@stomp/stompjs';
import { useRef, useEffect, useState, useCallback } from 'react';
import { Room } from '../type/type';

export const useRooms = () => {
  const { connected, subscribe, publish } = useWSContext();
  const [rooms, setRooms] = useState<Room[]>([]);
  const roomsSub = useRef<StompSubscription | null>(null);
  const createdSub = useRef<StompSubscription | null>(null);
  const errorSub = useRef<StompSubscription | null>(null);

  useEffect(() => {
    if (!connected) return;

    roomsSub.current = subscribe('/topic/rooms', (msg) => {
      const updated: Room[] = JSON.parse(msg.body);
      console.log(JSON.parse(msg.body), '/topic/rooms');
      setRooms(updated.filter((room) => !room.isFull)); // только здесь обновляем список комнат
    });

    errorSub.current = subscribe('/user/errors', (msg) => {
      const error = JSON.parse(msg.body);
      console.log(error);
    });

    createdSub.current = subscribe('/user/room.created', (room: IMessage) => {
      const roomW = JSON.parse(room.body);
      console.log(roomW);
    });

    publish('/app/room.list');

    return () => {
      roomsSub.current?.unsubscribe();
      createdSub.current?.unsubscribe();
    };
  }, [connected, subscribe, publish]);

  const createRoom = () => {
    publish('/app/room.create');
  };

  const joinRoom = async (roomId: string) => {
    publish('/app/room.join', JSON.stringify({ roomId }));
  };

  const leaveRoom = async (roomId: string) => {
    publish('/app/room.leave', JSON.stringify({ roomId }));
  };

  const deleteRoom = async (roomId: string) => {
    publish('/app/room.delete', JSON.stringify({ roomId }));
  };

  return {
    rooms,
    createRoom,
    joinRoom,
    connected,
    deleteRoom,
    leaveRoom,
  };
};
