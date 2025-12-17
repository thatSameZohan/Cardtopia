import { Client, IMessage, StompSubscription } from '@stomp/stompjs';
import { useState, useRef, useEffect, useCallback } from 'react';
import SockJS from 'sockjs-client';
import { API_URL } from '../config/env';
import { useAppSelector } from '@/redux/store';

type SubscriptionMap = Record<string, StompSubscription | null>;
type CallbackMap = Record<string, (message: IMessage) => void>;

export const useWS = () => {
  const [connected, setConnected] = useState(false);
  const clientRef = useRef<Client | null>(null);
  const subscriptionsRef = useRef<SubscriptionMap>({});
  const callbacksRef = useRef<CallbackMap>({});

  const { accessToken: token, isInitialLoad } = useAppSelector(
    (state) => state.auth,
  );

  // --- Создание и управление WS ---
  const initClient = useCallback(() => {
    if (!token) return;

    // если есть старый клиент — деактивируем
    if (clientRef.current) {
      clientRef.current.deactivate();
      clientRef.current = null;
    }

    const client = new Client({
      webSocketFactory: () => new SockJS(`${API_URL}/ws`),
      connectHeaders: { Authorization: `Bearer ${token}` },
      reconnectDelay: 5000, // автоматический reconnect
      onConnect: () => {
        setConnected(true);
        console.log('STOMP: Connected');

        // восстановление подписок после reconnect
        Object.entries(callbacksRef.current).forEach(([dest, callback]) => {
          if (!clientRef.current) return;
          subscriptionsRef.current[dest] = clientRef.current.subscribe(
            dest,
            callback,
          );
        });
      },
      onDisconnect: () => {
        setConnected(false);
        console.log('STOMP: Disconnected');
      },
      onStompError: (frame) => {
        console.error(
          'STOMP: Broker Error',
          frame.headers['message'],
          frame.body,
        );
      },
      onWebSocketError: (err) => console.error('STOMP: WebSocket Error', err),
    });

    client.activate();
    clientRef.current = client;
  }, [token]);

  useEffect(() => {
    if (isInitialLoad) return;

    if (!token) {
      // logout: отключаем WS
      clientRef.current?.deactivate();
      clientRef.current = null;
      subscriptionsRef.current = {};
      callbacksRef.current = {};
      setConnected(false);
      return;
    }

    initClient();

    return () => {
      clientRef.current?.deactivate();
      clientRef.current = null;
      subscriptionsRef.current = {};
      callbacksRef.current = {};
      setConnected(false);
    };
  }, [token, isInitialLoad, initClient]);

  // --- Подписка с сохранением ---
  const subscribe = useCallback(
    (destination: string, callback: (message: IMessage) => void) => {
      if (!clientRef.current?.connected) {
        console.error('STOMP: Not connected');
        return null;
      }

      const sub = clientRef.current.subscribe(destination, callback);
      subscriptionsRef.current[destination] = sub;
      callbacksRef.current[destination] = callback; // сохраняем callback отдельно
      return sub;
    },
    [],
  );

  // --- Публикация ---
  const publish = useCallback((destination: string, body?: string) => {
    if (!clientRef.current?.connected) {
      console.error('STOMP: Not connected');
      return;
    }
    clientRef.current.publish({ destination, body });
  }, []);

  return { connected, subscribe, publish };
};
