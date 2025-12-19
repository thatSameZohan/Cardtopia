'use client';
import { useWSContext } from '@/shared/ui/WSProvider/WSProvider';
import { StompSubscription } from '@stomp/stompjs';
import { useRef, useEffect, useState } from 'react';

export interface PrivateGameMessage {
  type: string;
  payload: any;
}

export interface PrivateGameState {
  myDeck: any[];
  hand: any[];
  privateEvents: PrivateGameMessage[];
}

export const usePrivateGame = (): PrivateGameState => {
  const { connected, subscribe } = useWSContext();

  const privateSub = useRef<StompSubscription | null>(null);
  const [myDeck, setMyDeck] = useState<any[]>([]);
  const [hand, setHand] = useState<any[]>([]);
  const [privateEvents, setPrivateEvents] = useState<PrivateGameMessage[]>([]);

  useEffect(() => {
    if (!connected) return;

    // Подписка на стартовую колоду и руку
    const initSub: StompSubscription | null = subscribe(
      '/user/queue/game.init',
      (msg) => {
        try {
          const data = JSON.parse(msg.body);
          setMyDeck(data.deck || []);
          setHand(data.hand || []);
        } catch {
          console.error('Invalid game.init message:', msg.body);
        }
      },
    );

    // Подписка на личные события
    privateSub.current?.unsubscribe();
    privateSub.current = subscribe('/user/queue/game.private', (msg) => {
      try {
        const parsed: PrivateGameMessage = JSON.parse(msg.body);
        setPrivateEvents((prev) => [...prev, parsed]);
      } catch {
        console.error('Invalid game.private message:', msg.body);
      }
    });

    return () => {
      if (initSub) initSub.unsubscribe();
      privateSub.current?.unsubscribe();
    };
  }, [connected, subscribe]);

  return { myDeck, hand, privateEvents };
};
