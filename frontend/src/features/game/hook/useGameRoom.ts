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
  const turnSub = useRef<StompSubscription | null>(null);
  const whoamiSub = useRef<StompSubscription | null>(null);

  const [messages, setMessages] = useState<GameMessage[]>([]);
  const [roomInfo, setRoomInfo] = useState<Room | null>(null);
  const [turn, setTurn] = useState<string | null>(null);
  const [myParticipantId, setMyParticipantId] = useState<string | null>(null);

  useEffect(() => {
    if (!connected) return;

    whoamiSub.current?.unsubscribe();
    whoamiSub.current = subscribe('/user/queue/whoami', (msg) => {
      if (!msg.body) return;
      setMyParticipantId(msg.body);
      publish(`/app/rooms/get/${roomId}`);
    });

    publish('/app/rooms/whoami');

    return () => whoamiSub.current?.unsubscribe();
  }, [connected, roomId, publish, subscribe]);

  useEffect(() => {
    if (!connected) return;

    infoSub.current?.unsubscribe();
    infoSub.current = subscribe(`/topic/rooms/${roomId}`, (msg) => {
      try {
        const room: Room = JSON.parse(msg.body);
        setRoomInfo(room);
      } catch {
        console.error('Invalid room info:', msg.body);
      }
    });

    return () => infoSub.current?.unsubscribe();
  }, [connected, roomId, subscribe]);

  useEffect(() => {
    if (!connected) return;

    roomSub.current?.unsubscribe();
    roomSub.current = subscribe(`/topic/room/${roomId}`, (msg) => {
      try {
        const parsed: GameMessage = JSON.parse(msg.body);
        setMessages((prev) => [...prev, parsed]);
      } catch {
        console.error('Invalid game message:', msg.body);
      }
    });

    return () => roomSub.current?.unsubscribe();
  }, [connected, roomId, subscribe]);

  useEffect(() => {
    if (!connected) return;

    turnSub.current?.unsubscribe();
    turnSub.current = subscribe(`/topic/room/${roomId}/turn`, (msg) => {
      try {
        const data = JSON.parse(msg.body);
        setTurn(data.currentTurn);
      } catch {
        console.error('Invalid turn message:', msg.body);
      }
    });

    return () => turnSub.current?.unsubscribe();
  }, [connected, roomId, subscribe]);

  const sendEvent = useCallback(
    (event: GameMessage) => {
      if (!connected) return;
      publish(`/app/room/${roomId}`, JSON.stringify(event));
    },
    [connected, publish, roomId],
  );

  const endTurn = useCallback(() => {
    if (!connected || !myParticipantId || turn !== myParticipantId) return;
    publish(`/app/room/${roomId}/endTurn`);
  }, [connected, publish, roomId, myParticipantId, turn]);

  const leaveRoom = useCallback(() => {
    if (!connected) return;
    publish(`/app/rooms/leave/${roomId}`);
    roomSub.current?.unsubscribe();
    infoSub.current?.unsubscribe();
    turnSub.current?.unsubscribe();
    roomSub.current = null;
    infoSub.current = null;
    turnSub.current = null;
  }, [connected, publish, roomId]);

  const deleteRoom = useCallback(() => {
    if (!connected) return;
    publish('/app/rooms/delete', roomId);
  }, [connected, publish, roomId]);

  const isMyTurn = turn && myParticipantId && turn === myParticipantId;

  return {
    messages,
    roomInfo,
    sendEvent,
    leaveRoom,
    connected,
    deleteRoom,
    endTurn,
    turn,
    myParticipantId,
    isMyTurn,
  };
};
