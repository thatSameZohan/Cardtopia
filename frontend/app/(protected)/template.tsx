import { cookies } from 'next/headers';
import { redirect } from 'next/navigation';
import { routes } from '@/shared/router/paths';

export default async function Template({ children }: { children: React.ReactNode }) {
  const cookieStore = cookies();
  const token = (await cookieStore).get('token');

  if (!token) {
    redirect(routes.login);
  }

  return <>{children}</>;
}