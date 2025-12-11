'use client';
import React from 'react';
import { useSelector } from 'react-redux';
import styles from './homepage.module.scss';

import Chat from '@/features/chat/ui/Chat';
import { RootState } from '@/redux/store'; // важно
import RoomList from '@/features/gameLobby/ui/RoomList';

export const HomepageView = () => {
  const username = useSelector((state: RootState) => state.auth.username);
  const isAuth = useSelector((state: RootState) => state.auth.isAuth);

  return (
    <main className={styles.main}>
      <div className={styles.text}>
        {isAuth ? `Привет, ${username}!` : 'Привет, гость 👋'}
      </div>
      <Chat />
      <RoomList />
    </main>
  );
};
