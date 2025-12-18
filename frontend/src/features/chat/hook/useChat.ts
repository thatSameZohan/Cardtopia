import { RootState, useAppSelector } from '@/redux/store';
import { useWSContext } from '@/shared/ui/WSProvider/WSProvider';
import { StompSubscription } from '@stomp/stompjs';
import { useCallback, useEffect, useRef, useState } from 'react';
export interface Message {
  sender: string;
  text: string;
}

export const useChat = () => {
  const { connected, subscribe, publish } = useWSContext();
  const [messages, setMessages] = useState<Message[]>([]);
  const [messageText, setMessageText] = useState('');
  const subscriptionRef = useRef<StompSubscription | null>(null);
  useEffect(() => {
    if (!connected || !subscribe) return;
    subscriptionRef.current?.unsubscribe?.();
    subscriptionRef.current = subscribe('/topic/chat', (msg) => {
      try {
        const m = JSON.parse(msg.body);
        setMessages((prev) => [...prev, m]);
      } catch (e) {
        console.error('useChat: invalid message', msg.body);
      }
    });

    return () => subscriptionRef.current?.unsubscribe?.();
  }, [connected, subscribe]);
  const sendMessage = useCallback(() => {
    if (!messageText.trim()) return;
    publish('/app/chat', messageText);
    setMessageText('');
  }, [messageText, publish]);

  return {
    messages,
    messageText,
    setMessageText,
    sendMessage,
    connected,
  };
};
