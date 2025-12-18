import GameRoom from '@/features/game/ui/GameRoom';
import { Metadata } from 'next';

export async function generateMetadata(): Promise<Metadata> {
  return { title: 'Игра' };
}
export default async function RoomPage({ params }: { params: { id: string } }) {
  return <GameRoom roomId={params.id} />;
}
