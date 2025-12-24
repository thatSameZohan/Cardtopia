import { type Metadata } from 'next';
import { GameView } from '@/features/game';
import { getMetadata } from 'shared/lib/metadata';
import { routes } from 'shared/router/paths';

type AsyncParams = {
  params: Promise<{ id: string }>;
};

export async function generateMetadata({
  params,
}: AsyncParams): Promise<Metadata> {
  const { id } = await params;

  return getMetadata({
    title: 'Игра',
    description: 'Игра в комнате',
    url: routes.game(id),
  });
}

export default async function RoomPage({ params }: AsyncParams) {
  const { id } = await params;

  return <GameView gameId={id} />;
}
