import React from 'react';
import { Button } from '@/shared/ui/Button';
import styles from './BossPanel.module.scss';

type BossPanelProps = {
  health: number;
  onAttack: () => void;
  disabled: boolean;
};

export const BossPanel: React.FC<BossPanelProps> = ({ health, onAttack, disabled }) => (
  <div className={styles.root}>
    <h1>Здоровье босса: {health}</h1>
    <Button disabled={disabled} onClick={onAttack}>
      Атаковать босса
    </Button>
  </div>
);
