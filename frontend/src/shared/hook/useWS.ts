import { Client, IMessage, StompSubscription } from '@stomp/stompjs';
import { useState, useRef, useEffect, useCallback } from 'react';
import SockJS from 'sockjs-client';
import { API_URL } from '../config/env';

export const useWS = () => {
  const [connected, setConnected] = useState(false);
  const clientRef = useRef<Client | null>(null);

  useEffect(() => {
    const client = new Client({
      webSocketFactory: () => new SockJS(`${API_URL}/ws`),
      reconnectDelay: 5000,
      onConnect: () => {
        setConnected(true);
        console.log('STOMP: Connected');
      },
      onDisconnect: () => {
        setConnected(false);
        console.log('STOMP: Disconnected');
      },
      onWebSocketError: (error) => console.error('STOMP: WebSocket Error', error),
      onStompError: (frame) => console.error('STOMP: Broker Error', frame.headers['message'], frame.body),
    });

    client.activate();
    clientRef.current = client;

    return () => {
      client.deactivate().catch(console.error);
    };
  }, []);

  const subscribe = useCallback(
    (destination: string, callback: (message: IMessage) => void): StompSubscription | null => {
      if (clientRef.current && clientRef.current.active) return clientRef.current.subscribe(destination, callback);
      console.error('STOMP: Cannot subscribe, client is not active.');
      return null;
    },
    [],
  );

  const publish = useCallback((destination: string, body?: string) => {
    if (clientRef.current && clientRef.current.active) clientRef.current.publish({ destination, body });
    else console.error('STOMP: Cannot publish, client is not active.');
  }, []);

  return { connected, subscribe, publish };
};
