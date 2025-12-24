'use client';

import React, { useEffect } from 'react';
import { useGame } from '../../hook/useGame';
import styles from './Game.module.scss';
import { Table } from './Table';
import { RootState, useAppSelector } from '@/redux/store';
import { DndProvider } from 'react-dnd';
import { HTML5Backend } from 'react-dnd-html5-backend';
type Props = { gameId: string };

export const GameView = ({ gameId }: Props) => {
  const { gameState, playCard, buyCard, attack, endTurn } = useGame(gameId);
  const username = useAppSelector((state: RootState) => state.auth.username);

  useEffect(() => {
    if (!gameId) {
      console.warn('[GameView] no gameId');
    }
  }, [gameId]);

  if (!gameState) {
    return <div>Waiting for game state…</div>;
  }

  return (
    <div className={styles.game}>
      <h3 className={styles['game__title']}>Game: {gameId}</h3>
      <DndProvider backend={HTML5Backend}>
        <Table
          stateGame={gameState}
          username={username}
          onPlayCard={playCard}
          onBuyCard={buyCard}
          onEndTurn={endTurn}
          onAttack={attack}
        />
      </DndProvider>
    </div>
  );
};
