'use client';

import { Dialog, DialogDismiss, useDialogStore } from '@ariakit/react';
import { useEffect } from 'react';
import styles from './Game.module.scss';
import { GameState } from '../../type/type';

type Props = {
  gameState: GameState;
  onPlayAgain: () => void;
  onLeave: () => void;
};

export const GameResultDialog = ({
  gameState,
  onPlayAgain,
  onLeave,
}: Props) => {
  const dialog = useDialogStore();

  useEffect(() => {
    if (gameState.winnerId) {
      dialog.show();
    } else {
      dialog.hide();
    }
  }, [dialog, gameState.winnerId]);

  if (!gameState.winnerId) {
    return null;
  }

  const isWin = gameState.winnerId === gameState.activePlayerId;

  return (
    <>
      {/* модалка */}
      <Dialog
        store={dialog}
        className={styles.dialog}
        backdrop={<div className={styles.backdrop} />}
      >
        <h1 className={styles.title}>{isWin ? '🏆 Победа' : '💀 Поражение'}</h1>
        <p className={styles.subtitle}>Победитель: {gameState.winnerId}</p>

        <div className={styles.actions}>
          <button onClick={onPlayAgain}>Сыграть ещё</button>
          <DialogDismiss onClick={onLeave}>Выйти</DialogDismiss>
        </div>
      </Dialog>
    </>
  );
};
