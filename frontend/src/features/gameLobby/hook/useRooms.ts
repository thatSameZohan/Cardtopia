import { useWSContext } from '@/shared/ui/WSProvider/WSProvider';
import { StompSubscription } from '@stomp/stompjs';
import { useRef, useEffect, useState, useCallback } from 'react';

interface Room {
  id: string;
  name: string;
  participantsCount: number;
}

export const useRooms = () => {
  const { connected, subscribe, publish } = useWSContext();
  const [rooms, setRooms] = useState<Room[]>([]);
  const subscriptionRef = useRef<StompSubscription | null>(null);

  useEffect(() => {
    if (!connected) return;

    subscriptionRef.current = subscribe('/topic/rooms', (msg) => {
      const updated: Room[] = JSON.parse(msg.body);
      setRooms(updated); // только здесь обновляем список комнат
    });

    publish('/app/rooms/list');

    return () => subscriptionRef.current?.unsubscribe();
  }, [connected, subscribe, publish]);

  const createRoom = (): Promise<string> =>
    new Promise((resolve) => {
      // Временная обработка созданной комнаты
      const handleCreated = (msg: any) => {
        const created: Room = JSON.parse(msg.body);
        setRooms((prev) => [...prev, created]); // добавляем сразу в состояние
        resolve(created.id);
        sub?.unsubscribe();
      };

      const sub = subscribe('/user/queue/rooms/created', handleCreated);
      publish('/app/rooms/add', 'Новая комната');
    });

  const joinRoom = async (roomId: string) => roomId;
  const deleteRoom = async (roomId: string) =>
    publish('/app/rooms/delete', roomId);
  return { rooms, createRoom, joinRoom, connected, deleteRoom };
};
