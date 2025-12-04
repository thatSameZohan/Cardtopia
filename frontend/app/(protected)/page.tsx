import { HomepageView } from '@/pages/homepage';
import { type Metadata } from 'next/types';

export async function generateMetadata(): Promise<Metadata> {
  return { title: 'Auth Test' };
}

export default function AuthTestPage() {
  return <HomepageView />;
}
