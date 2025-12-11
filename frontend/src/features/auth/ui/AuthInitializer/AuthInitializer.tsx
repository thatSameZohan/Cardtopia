import { useEffect } from 'react';
import { useRefreshTokenMutation } from '@/redux/auth/auth';
import { useAppDispatch, useAppSelector } from '@/redux/store';
import { setInitialLoad } from '@/redux/auth/authSlice';

export const AuthInitializer = ({ children }: { children: React.ReactNode }) => {
  const [refreshToken] = useRefreshTokenMutation();
  const dispatch = useAppDispatch();
  const { isInitialLoad } = useAppSelector((state) => state.auth);

  useEffect(() => {
    const initialize = async () => {
      try {
        await refreshToken().unwrap();
      } catch (error) {
        // Ошибки обрабатываются в `refreshToken` onQueryStarted
        // и в `baseQueryWithReauth`
      } finally {
        // Гарантируем, что isInitialLoad станет false
        // даже если `refreshToken` не вызовется (например, из-за кэша)
        dispatch(setInitialLoad(false));
      }
    };

    // Запускаем проверку только один раз при старте
    if (isInitialLoad) {
      initialize();
    }
  }, [dispatch, refreshToken, isInitialLoad]);

  return <>{children}</>;
};
