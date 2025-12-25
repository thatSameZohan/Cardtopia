import { Message } from './../../chat/hook/useChat';
import { useWSContext } from '@/shared/ui/WSProvider/WSProvider';
import { StompSubscription } from '@stomp/stompjs';
import { useEffect, useRef, useState } from 'react';
import { GameState, Player } from '../type/type';
import { toast } from 'react-toastify';

export const useGame = (gameId: string | null) => {
  const { connected, subscribe, publish } = useWSContext();

  const [gameState, setGameState] = useState<GameState | null>(null);
  const [player, setPlayer] = useState<Player | null>(null);
  const queueSubRef = useRef<StompSubscription | null>(null);
  const topicSubRef = useRef<StompSubscription | null>(null);
  const errorSubRef = useRef<StompSubscription | null>(null);
  useEffect(() => {
    if (!connected || !gameId) return;
    // защита от повторных подписок
    queueSubRef.current?.unsubscribe();
    topicSubRef.current?.unsubscribe();

    // 1️⃣ initial snapshot (personal)
    queueSubRef.current = subscribe(`/user/queue/game.${gameId}`, (msg) => {
      try {
        console.log(JSON.parse(msg.body), '/user/queue/game');
        setPlayer(JSON.parse(msg.body));
      } catch (e) {
        console.error('[useGame] queue parse error', e);
      }
    });

    errorSubRef.current = subscribe('/user/queue/errors', (msg) => {
      const { message } = JSON.parse(msg.body);
      toast.error(message);
    });

    // 2️⃣ updates (broadcast)
    topicSubRef.current = subscribe(`/topic/game.${gameId}`, (msg) => {
      try {
        console.log(JSON.parse(msg.body), `/topic/game.${gameId}`);
        setGameState(JSON.parse(msg.body));
      } catch (e) {
        console.error('[useGame] topic parse error', e);
      }
    });

    // 3️⃣ запрос initial state
    publish('/app/game.init', JSON.stringify({ gameId }));

    return () => {
      queueSubRef.current?.unsubscribe();
      topicSubRef.current?.unsubscribe();
      queueSubRef.current = null;
      topicSubRef.current = null;
    };
  }, [connected, gameId, subscribe, publish]);

  /* ===== ACTIONS ===== */

  const playCard = (cardId: string) =>
    publish('/app/game.playCard', JSON.stringify({ gameId, cardId }));

  const buyCard = (marketCardId: string) =>
    publish('/app/game.buyCard', JSON.stringify({ gameId, marketCardId }));

  const attack = () => publish('/app/game.attack', JSON.stringify({ gameId }));

  const endTurn = () =>
    publish('/app/game.endTurn', JSON.stringify({ gameId }));

  return {
    player,
    gameState,
    playCard,
    buyCard,
    attack,
    endTurn,
    connected,
  };
};
