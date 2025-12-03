import { api } from '../api';
import { toast } from 'react-toastify';
import { apiPaths } from '@/shared/config/api-paths';
import { LoginInput, RegisterInput } from '@/features/auth/model/auth';
import { logout, setTokens } from './authSlice';

export type RegisterResponse = object;
export type LoginResponse = { access_token: string; userId: number; };

export type RefreshTokenResponse = { token: string;};
export type RefreshTokenInput = { refresh_token: string };

export const authApi = api.injectEndpoints({
  overrideExisting: true,
  endpoints: (builder) => ({
    login: builder.mutation<LoginResponse, LoginInput>({
      query: (body) => ({
        url: apiPaths.auth.login,
        credentials: 'include',
        method: 'POST',
        body,
      }),
      onQueryStarted: async (_, { dispatch, queryFulfilled, getCacheEntry }) => {
        try {
          const { data } = await queryFulfilled;
          console.log("authApi.login.onQueryStarted", data);
          toast.success('Вы успешно вошли в систему');
          if (data) {
            dispatch(setTokens({
              accessToken: data.access_token,
            }));
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
     credentials: 'include',
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
        url: apiPaths.auth.refresh,
        credentials: 'include',
        method: 'POST',
        body,
      }),
    }),
    logout: builder.mutation<void, void>({
      query: () => ({
        url: apiPaths.auth.logout,
         credentials: 'include',
          method: 'POST',
      }),
      onQueryStarted: async (_, { dispatch, queryFulfilled }) => {
        try {
           const { data } = await queryFulfilled;
          console.log('logout', data);

           dispatch(logout());
        } catch (error) {
        
           dispatch(logout());
        }
      },
    }),
  }),
});

export const { useLoginMutation, useRegisterMutation, useRefreshTokenMutation, useLogoutMutation } = authApi;
