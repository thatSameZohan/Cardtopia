import { api } from '../api';
import { toast } from 'react-toastify';
import { apiPaths } from '@/shared/config/api-paths';
import { LoginInput, RegisterInput } from '@/features/auth/model/auth';
import { logout, setTokens } from './authSlice';

export type RegisterResponse = object;
export type LoginResponse = { accessToken: string; refresh_token: string; userId: number };

export type RefreshTokenResponse = { token: string; refresh_token: string };
export type RefreshTokenInput = { refresh_token: string };

export const authApi = api.injectEndpoints({
  overrideExisting: true,
  endpoints: (builder) => ({
    login: builder.mutation<LoginResponse, LoginInput>({
      query: (body) => ({
        url: apiPaths.auth.login,
        method: 'POST',
        body,
      }),
      onQueryStarted: async (_, { dispatch, queryFulfilled, getCacheEntry }) => {
        try {
          const { data } = await queryFulfilled;
          toast.success('Вы успешно вошли в систему');
          if (data) {
            dispatch(setTokens({ accessToken: data.accessToken, refreshToken: data.refresh_token }));
          }
        } catch (error) {
          dispatch(logout({ noRedirect: true }));
          throw error;
        }
      },
    }),
    register: builder.mutation<RegisterResponse, RegisterInput>({
      query: (body) => ({
        url: apiPaths.auth.register,
        method: 'POST',
        body,
      }),
      onQueryStarted: async (_, { queryFulfilled }) => {
        try {
          await queryFulfilled;
          toast.success('Вы успешно зарегистрировались');
        } catch (error) {
          // Ошибка будет обработана в middleware
        }
      },
    }),
    refreshToken: builder.mutation<RefreshTokenResponse, RefreshTokenInput>({
      query: (body) => ({
        url: apiPaths.auth.refreshToken,
        method: 'POST',
        body,
      }),
    }),
  }),
});

export const { useLoginMutation, useRegisterMutation, useRefreshTokenMutation } = authApi;
