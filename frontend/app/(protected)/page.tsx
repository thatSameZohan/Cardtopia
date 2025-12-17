import { HomepageView } from '@/pages/homepage';
import { type Metadata } from 'next/types';

export async function generateMetadata(): Promise<Metadata> {
  return { title: 'Лобби игры' };
}

export default function AuthTestPage() {
  return <HomepageView />;
}
