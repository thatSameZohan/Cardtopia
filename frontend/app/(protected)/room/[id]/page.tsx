import GameRoom from '@/features/game/ui/GameRoom';

export default function RoomPage({ params }: { params: { id: string } }) {
  return <GameRoom roomId={params.id} />;
}
