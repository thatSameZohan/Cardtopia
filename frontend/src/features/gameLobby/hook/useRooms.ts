import { useWSContext } from '@/shared/ui/WSProvider/WSProvider';
import { StompSubscription, IMessage } from '@stomp/stompjs';
import { useRef, useEffect, useState } from 'react';
import { Room } from '../type/type';
import { toast } from 'react-toastify';
import { useRouter } from 'next/navigation';

export const useRooms = (roomId?: string) => {
  const { connected, subscribe, publish } = useWSContext();
  const [rooms, setRooms] = useState<Room[]>([]);

  const roomsSub = useRef<StompSubscription | null>(null);
  const createdSub = useRef<StompSubscription | null>(null);
  const gameStartSub = useRef<StompSubscription | null>(null);
  const errorSub = useRef<StompSubscription | null>(null);

  const router = useRouter();

  useEffect(() => {
    if (!connected) return;

    // Получение списка комнат
    roomsSub.current = subscribe('/topic/rooms', (msg) => {
      const updated: Room[] = JSON.parse(msg.body);
      setRooms(updated.filter((r) => r));
    });

    // Ошибки
    errorSub.current = subscribe(`/user/queue/errors`, (msg: IMessage) => {
      const text =
        typeof msg.body === 'string' ? msg.body : JSON.stringify(msg.body);
      toast.error(text);
    });

    // Новые комнаты
    createdSub.current = subscribe(
      `/user/queue/room.created`,
      (msg: IMessage) => {
        const room = JSON.parse(msg.body);
        if (!room.id) toast.error('Ошибка при создании комнаты');
        else router.push(`/room/${room.id}`);
      },
    );

    // Старт игры
    if (roomId) {
      gameStartSub.current = subscribe(
        `/topic/game.start.${roomId}`,
        (msg: IMessage) => {
          const { gameId } = JSON.parse(msg.body);
          router.replace(`/game/${gameId}`);
        },
      );
    }

    // Запросить список комнат
    publish('/app/room.list');

    return () => {
      roomsSub.current?.unsubscribe();
      createdSub.current?.unsubscribe();
      gameStartSub.current?.unsubscribe();
      errorSub.current?.unsubscribe();
    };
  }, [connected, subscribe, publish, roomId]);

  return {
    rooms,
    createRoom: () => publish('/app/room.create'),
    joinRoom: (id: string) =>
      publish('/app/room.join', JSON.stringify({ roomId: id })),
    leaveRoom: (id: string) =>
      publish('/app/room.leave', JSON.stringify({ roomId: id })),
    deleteRoom: (id: string) =>
      publish('/app/room.delete', JSON.stringify({ roomId: id })),
    connected,
  };
};
