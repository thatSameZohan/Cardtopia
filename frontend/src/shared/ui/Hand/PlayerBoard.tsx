import React from 'react';
import { useSelector } from 'react-redux';
import { RootState } from '../../../redux/store';
import { Player } from '../../../redux/gameSlice/gameSlice';

export const PlayerBoard = ({ playerId }: { playerId: string }) => {
  const state = useSelector((s: RootState) => s.game);
  const player = state.players.find((p: Player) => p.id === playerId);
  if(!player) return null;

  return (
    <div className="player-board">
      <div>HP: {player.health}</div>
      <div>Gold: {player.gold}</div>
      <div>Deck: {player.deck.length}</div>
      <div>Discard: {player.discard.length}</div>
    </div>
  );
};
