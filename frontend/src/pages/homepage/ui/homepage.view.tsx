'use client';
import React from 'react';
import { useSelector } from 'react-redux';
import { DndProvider } from 'react-dnd';
import { HTML5Backend } from 'react-dnd-html5-backend';
import styles from './homepage.module.scss';

import Chat from '@/features/chat/ui/Chat';
import { RootState } from '@/redux/store'; // важно

export const HomepageView = () => {
  const username = useSelector((state: RootState) => state.auth.username);
  const isAuth = useSelector((state: RootState) => state.auth.isAuth);

  return (
    <DndProvider backend={HTML5Backend}>
      <div>
        {isAuth ? `Привет, ${username}!` : 'Привет, гость 👋'}
      </div>

      <main className={styles.main}>
        <Chat />
      </main>
    </DndProvider>
  );
};
