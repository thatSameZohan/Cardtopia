import { type Metadata } from 'next';

import { RegistrView } from 'pages/registr';
import { getMetadata } from 'shared/lib/metadata';
import { routes } from 'shared/router/paths';

export async function generateMetadata(): Promise<Metadata> {
  return getMetadata({
    title: 'Регистрация',
    description: 'Регистрация',
    url: routes.login,
  });
}

export default async function Login() {
  return <RegistrView />;
}
