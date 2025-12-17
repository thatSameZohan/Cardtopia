'use client';

import { useEffect, useRef } from 'react';
import { useChat } from '../hook/useChat';

import styles from './Chat.module.scss';
import { RootState, useAppSelector } from '@/redux/store';

export default function Chat() {
  const { messages, messageText, setMessageText, sendMessage, connected } =
    useChat();
  const username = useAppSelector((state: RootState) => state.auth.username);
  const messagesEndRef = useRef<HTMLDivElement | null>(null);

  // Прокрутка вниз при новом сообщении
  useEffect(() => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  }, [messages]);

  return (
    <div className={styles.chat_window}>
      <header className={styles.header}>
        <h2 className={styles.header_title}>Чат</h2>
        <div className={styles.header_status}>
          <span
            className={styles.header_statusDot}
            style={{
              backgroundColor: connected
                ? 'var(--success-color)'
                : 'var(--error-color)',
            }}
          />
          {connected ? 'Подключено' : 'Отключено'}
        </div>
      </header>

      <div className={styles.messages_container}>
        {messages.map((m, i) => (
          <div
            key={i}
            className={`${styles.message} ${m.sender === username ? styles.message_own : styles.message_other}`}
          >
            <div className={styles.message_bubble}>
              {m.sender && (
                <div className={styles.message_sender}>{m.sender}</div>
              )}
              <div className={styles.message_text}>{m.text}</div>
            </div>
          </div>
        ))}
        <div ref={messagesEndRef} />
      </div>

      <div className={styles.input_area}>
        <input
          value={messageText}
          onChange={(e) => setMessageText(e.target.value)}
          onKeyDown={(e) => e.key === 'Enter' && sendMessage()}
          placeholder={'Напишите сообщение...'}
        />
        <button onClick={sendMessage} disabled={!messageText.trim()}>
          ➤
        </button>
      </div>
    </div>
  );
}
