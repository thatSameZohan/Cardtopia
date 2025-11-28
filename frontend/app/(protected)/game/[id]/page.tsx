'use client';
import React, { useEffect } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { Hand } from '@/shared/ui/Hand/Hand';
import { Market } from '@/shared/ui/Hand/Market';
import { PlayerBoard } from '@/shared/ui/Hand/PlayerBoard';
import { connect, disconnect } from '@/redux/gameSlice/gameSlice';
import { RootState } from '@/redux/store';

interface RoomPageProps {
  params: {
    id: string;
  };
}

const RoomPage = ({ params }: RoomPageProps) => {
  const dispatch = useDispatch();
  const roomId = params.id;
  const playerId = useSelector((state: RootState) => state.auth.userId);

  useEffect(() => {
    if (roomId && playerId) {
      dispatch(connect({ roomId, playerId: String(playerId) }));
    }
    return () => {
      dispatch(disconnect());
    };
  }, [roomId, playerId, dispatch]);

  return (
    <div className="game-room">
      {playerId && <PlayerBoard playerId={String(playerId)} />}
      <Hand />
      <Market />
      {/* Здесь можно добавить OpponentBoard */}
    </div>
  );
};

export default RoomPage;
