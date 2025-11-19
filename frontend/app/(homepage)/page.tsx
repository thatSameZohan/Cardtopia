import { type Metadata } from 'next';
import { HomepageView } from 'pages/homepage';
import { homepageApi } from 'pages/homepage';

export async function generateMetadata(): Promise<Metadata> {
  return { title: 'Homepage' };
}

export default async function Homepage() {
  const data = await homepageApi.get();
  return <HomepageView {...data} />;
}
