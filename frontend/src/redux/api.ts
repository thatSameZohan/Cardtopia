import { createApi, fetchBaseQuery } from '@reduxjs/toolkit/query/react';
import { BaseQueryFn, FetchArgs, FetchBaseQueryError } from '@reduxjs/toolkit/query';
import { Mutex } from 'async-mutex';
import { API_URL } from '@/shared/config/env';
import { apiPaths } from '@/shared/config/api-paths';
import { setTokens, logout } from '@/redux/auth/authSlice';
import { RootState } from '@/redux/store';

const baseQuery = fetchBaseQuery({
  baseUrl: `${API_URL}/v1/api`,
  credentials: 'include',
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

  if (result.error?.status === 401) {
    const requestUrl = typeof args === 'string' ? args : args.url;
    const isRefreshRequest = requestUrl === apiPaths.auth.refresh;

    if (isRefreshRequest) {
      api.dispatch(logout());
      return result;
    }

    if (!mutex.isLocked()) {
      const release = await mutex.acquire();
      try {
        const refreshRes = await baseQuery({ url: apiPaths.auth.refresh, method: 'POST' }, api, extraOptions);

        if (refreshRes.data) {
          const { accessToken } = refreshRes.data as { accessToken: string };
          api.dispatch(setTokens({ accessToken }));

          result = await baseQuery(args, api, extraOptions);
        } else {
          api.dispatch(logout());
        }
      } finally {
        release();
      }
    } else {
      await mutex.waitForUnlock();
      result = await baseQuery(args, api, extraOptions);
    }
  }

  if (result.error?.status === 403) {
  }

  return result;
};

export const api = createApi({
  baseQuery: baseQueryWithReauth,
  endpoints: (builder) => ({
    getMe: builder.query<any, void>({
      query: () => '/me',
    }),
  }),
});

export const { useGetMeQuery, useLazyGetMeQuery } = api;
