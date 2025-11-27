import { createApi, fetchBaseQuery } from '@reduxjs/toolkit/query/react';
import { BaseQueryFn, FetchArgs, FetchBaseQueryError } from '@reduxjs/toolkit/query';
import { Mutex } from 'async-mutex';
import { API_URL } from '@/shared/config/env';
import { apiPaths } from '@/shared/config/api-paths';
import { setTokens, logout } from '@/redux/auth/authSlice';
import { RootState } from '@/redux/store';
import Cookies from 'js-cookie'; // Добавляем импорт Cookies

const baseQuery = fetchBaseQuery({
  baseUrl: `${API_URL}/v1/api`,
  credentials: 'include', // Добавляем эту строку
  prepareHeaders: (headers, { getState }) => {
    const token = (getState() as RootState).auth.accessToken;
    if (token && !headers.has('Authorization')) {
      headers.set('Authorization', `Bearer ${token}`);
    }
    return headers;
  },
});

const mutex = new Mutex();

const baseQueryWithReauth: BaseQueryFn<string | FetchArgs, unknown, FetchBaseQueryError> = async (
  args,
  api,
  extraOptions,
) => {
  await mutex.waitForUnlock();

  let result = await baseQuery(args, api, extraOptions);

  if (result.error && result.meta?.response?.status === 401) {
    console.warn('Получен 401 Unauthorized, попытка обновления токена...');
    const refreshToken = Cookies.get('refresh_token'); // Получаем refreshToken из cookie
    if (!refreshToken) {
      console.error('RefreshToken отсутствует, перенаправление на страницу входа.');
      api.dispatch(logout({ noRedirect: true }));
      return result;
    }

    if (!mutex.isLocked()) {
      const release = await mutex.acquire();
      try {
        const refreshResult = await baseQuery(
          {
            url: apiPaths.auth.refresh,
            method: 'POST',
            // refreshToken отправляется в cookie, поэтому здесь его не передаем в body
          },
          api,
          extraOptions,
        );

        if (refreshResult.data) {
          const { token, refresh_token } = refreshResult.data as { token: string; refresh_token: string };
          api.dispatch(setTokens({ accessToken: token, refreshToken: refresh_token }));

          result = await baseQuery(args, api, extraOptions);
        } else {
          console.error('Ошибка при обновлении RefreshToken, перенаправление на страницу входа.');
          api.dispatch(logout({ noRedirect: true }));
        }
      } finally {
        release();
      }
    } else {
      await mutex.waitForUnlock();
      result = await baseQuery(args, api, extraOptions);
    }
  }

  return result;
};

export const api = createApi({
  baseQuery: baseQueryWithReauth,
  endpoints: () => ({}),
});
