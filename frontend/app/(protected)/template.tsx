import { cookies } from 'next/headers';
import { redirect } from 'next/navigation';
import { routes } from '@/shared/router/paths';

export default async function Template({ children }: { children: React.ReactNode }) {
  const cookieStore = cookies();
  const token = (await cookieStore).get('access_token');
  console.log('Template.tsx: Получен access_token:', token ? token.value : 'нет');

  if (!token) {
    redirect(routes.login);
  }

  return <>{children}</>;
}