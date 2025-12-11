import { cookies } from 'next/headers';
import { redirect } from 'next/navigation';
import { routes } from '@/shared/router/paths';
import { ProtectedClientWrapper } from '@/shared/ui/ProtectedClientWrapper';
import { WSProvider } from '@/shared/ui/WSProvider';

export default async function ProtectedTemplate({ children }: { children: React.ReactNode }) {
  const cookieStore = await cookies();
  const refreshToken = cookieStore.get('refresh_token'); // проверяем SSR cookie

  if (!refreshToken) redirect(routes.login);

  return (
    <ProtectedClientWrapper>
      <WSProvider>{children}</WSProvider>
    </ProtectedClientWrapper>
  );
}
