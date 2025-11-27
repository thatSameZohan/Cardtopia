
import { type Metadata } from 'next/types';
import { HomepageView } from '@/pages/homepage';



export async function generateMetadata(): Promise<Metadata> {
  return { title: 'Homepage' };
}

export default async function Homepage() {
  return <HomepageView /> 
}
