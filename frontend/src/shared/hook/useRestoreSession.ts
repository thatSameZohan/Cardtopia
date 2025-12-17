'use client';
import { useEffect } from 'react';
import { useDispatch } from 'react-redux';
import { useRouter } from 'next/navigation';

import { setTokens, logout, restoreSession } from '@/redux/auth/authSlice';
import { useLazyGetMeQuery } from '@/redux/api';
import { useRefreshTokenMutation } from '@/redux/auth/auth';
import { toast } from 'react-toastify';
import { routes } from '../router/paths';

export function useRestoreSession() {
  const dispatch = useDispatch();
  const [getMe] = useLazyGetMeQuery();
  const [refreshToken] = useRefreshTokenMutation();
  const router = useRouter();

  useEffect(() => {
    (async () => {
      try {
        // Пытаемся получить текущего пользователя
        const meResult = await getMe().unwrap();
        dispatch(
          restoreSession({
            username: meResult.username,
          }),
        );
      } catch (err: any) {
        // Если 401/403 → пробуем обновить accessToken
        if (err?.status === 401 || err?.status === 403) {
          try {
            const refreshRes = await refreshToken().unwrap();
            dispatch(setTokens({ accessToken: refreshRes.accessToken }));

            // После успешного обновления accessToken повторно вызываем /me
            const meResult = await getMe().unwrap();
            dispatch(
              setTokens({
                accessToken: refreshRes.accessToken,
                username: meResult.username,
              }),
            );
          } catch (refreshErr: any) {
            // Если refresh не удался или пришёл 440 → полный logout
            dispatch(logout());
            toast.error('Ваша сессия истекла, пожалуйста, войдите снова');
            router.push(routes.login);
          }
        }
      }
    })();
  }, [dispatch, getMe, refreshToken]);
}
