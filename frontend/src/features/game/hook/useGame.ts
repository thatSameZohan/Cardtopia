import { Room } from '@/features/gameLobby/type/type';
import { useWSContext } from '@/shared/ui/WSProvider/WSProvider';
import { StompSubscription } from '@stomp/stompjs';
import { useRef, useEffect, useState } from 'react';

export interface GameMessage {
  type: string;
  payload: any;
}

export const useGame = (room: Room) => {
  const { connected, subscribe, publish } = useWSContext();

  const gameSub = useRef<StompSubscription | null>(null);
  const [messages, setMessages] = useState<GameMessage[]>([]);

  useEffect(() => {
    if (!connected) return;

    gameSub.current?.unsubscribe();
    gameSub.current = subscribe(`/topic/game/${room.id}`, (msg) => {
      try {
        const parsed: GameMessage = JSON.parse(msg.body);
        setMessages((prev) => [...prev, parsed]);
      } catch {
        console.error('Invalid game message:', msg.body);
      }
    });

    return () => gameSub.current?.unsubscribe();
  }, [connected, room.id, subscribe]);

  return { messages };
};
