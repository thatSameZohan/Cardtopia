import { useWSContext } from '@/shared/ui/WSProvider/WSProvider';
import { StompSubscription, IMessage } from '@stomp/stompjs';
import { useRef, useEffect, useState } from 'react';
import { Room } from '../type/type';
import { toast } from 'react-toastify';
import { useRouter } from 'next/navigation';

export const useRooms = () => {
  const { connected, subscribe, publish } = useWSContext();
  const [rooms, setRooms] = useState<Room[]>([]);
  const [room, setRoom] = useState<Room>();
  const roomsSub = useRef<StompSubscription | null>(null);
  const createdSub = useRef<StompSubscription | null>(null);
  const errorSub = useRef<StompSubscription | null>(null);
  const router = useRouter();
  useEffect(() => {
    if (!connected) return;

    roomsSub.current = subscribe('/topic/rooms', (msg) => {
      const updated: Room[] = JSON.parse(msg.body);
      setRooms(updated.filter((room) => room)); // только здесь обновляем список комнат
    });

    errorSub.current = subscribe(`/user/queue/errors`, (msg: IMessage) => {
      const text =
        typeof msg.body === 'string' ? msg.body : JSON.stringify(msg.body);
      toast.error(text);
    });

    createdSub.current = subscribe(
      `/user/queue/room.created`,
      (message: IMessage) => {
        const room = JSON.parse(message.body);
        console.log(room, 'useRooms');
        setRoom(room);
        if (!room.id) {
          toast.error('Ошибка при создании комнаты');
          return;
        }
        router.push(`/room/${room.id}`);
      },
    );

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
    room,
  };
};
