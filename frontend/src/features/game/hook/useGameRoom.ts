import { Room } from '@/features/gameLobby/hook/useRooms';
import { useWSContext } from '@/shared/ui/WSProvider/WSProvider';
import { StompSubscription } from '@stomp/stompjs';
import { useRef, useEffect, useState, useCallback } from 'react';

export interface GameMessage {
  type: string;
  payload: any;
}

export const useGameRoom = (roomId: string) => {
  const { connected, subscribe, publish } = useWSContext();

  const roomSub = useRef<StompSubscription | null>(null);
  const infoSub = useRef<StompSubscription | null>(null);

  const [messages, setMessages] = useState<GameMessage[]>([]);
  const [roomInfo, setRoomInfo] = useState<Room | null>(null);

  // подписка на игровые события
  useEffect(() => {
    if (!connected) return;

    roomSub.current?.unsubscribe();

    roomSub.current = subscribe(`/topic/room/${roomId}`, (msg) => {
      try {
        const parsed = JSON.parse(msg.body);
        setMessages((prev) => [...prev, parsed]);
      } catch (e) {
        console.error('Invalid game message:', msg.body);
      }
    });
    return () => roomSub.current?.unsubscribe();
  }, [connected, roomId]);

  // подписка на состояние комнаты
  useEffect(() => {
    if (!connected) return;

    infoSub.current?.unsubscribe();
    infoSub.current = subscribe(`/topic/rooms/${roomId}`, (msg) => {
      try {
        setRoomInfo(JSON.parse(msg.body));
      } catch (e) {
        console.error('Invalid room info:', msg.body);
      }
    });

    // запрос состояния комнаты
    publish(`/app/rooms/get/${roomId}`);

    return () => infoSub.current?.unsubscribe();
  }, [connected, roomId]);

  const sendEvent = useCallback(
    (event: GameMessage) => {
      publish(`/app/room/${roomId}`, JSON.stringify(event));
    },
    [publish, roomId],
  );

  const leaveRoom = useCallback(() => {
    publish(`/app/rooms/leave/${roomId}`);
    roomSub.current?.unsubscribe();
    infoSub.current?.unsubscribe();
    roomSub.current = null;
    infoSub.current = null;
  }, [publish, roomId]);

  const deleteRoom = useCallback(() => {
    publish('/app/rooms/delete', roomId);
  }, [publish, roomId]);

  return { messages, roomInfo, sendEvent, leaveRoom, connected, deleteRoom };
};
