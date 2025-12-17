import { LoginView } from '@/pages/login';
import { getMetadata } from '@/shared/lib/metadata';
import { routes } from '@/shared/router/paths';
import { type Metadata } from 'next';

export async function generateMetadata(): Promise<Metadata> {
  return getMetadata({
    title: 'Вход в личный кабинет',
    description: 'Вход в личный кабинет',
    url: routes.login,
  });
}

export default async function Login() {
  return <LoginView />;
}
