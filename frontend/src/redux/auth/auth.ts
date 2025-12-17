import { api } from '../api';
import { toast } from 'react-toastify';
import { apiPaths } from '@/shared/config/api-paths';
import { LoginInput, RegisterInput } from '@/features/auth/model/auth';
import { logout, setTokens } from './authSlice';

export type RegisterResponse = object;
export type LoginResponse = { accessToken: string; userId: number };

export type RefreshTokenResponse = { token: string };
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
      onQueryStarted: async (
        _,
        { dispatch, queryFulfilled, getCacheEntry },
      ) => {
        try {
          const { data } = await queryFulfilled;
          toast.success('Вы успешно вошли в систему');
          if (data) {
            dispatch(
              setTokens({
                accessToken: data.accessToken,
              }),
            );
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
        } catch (error) {}
      },
    }),
    refreshToken: builder.mutation<LoginResponse, void>({
      query: () => ({
        url: apiPaths.auth.refresh,
        credentials: 'include',
        method: 'POST',
      }),
      onQueryStarted: async (_, { dispatch, queryFulfilled }) => {
        try {
          const { data } = await queryFulfilled;
          if (data) {
            const meData = await dispatch(
              api.endpoints.getMe.initiate(),
            ).unwrap();
            dispatch(
              setTokens({
                accessToken: data.accessToken,
                username: meData.username,
              }),
            );
          }
        } catch (error) {
          dispatch(logout({ noRedirect: true }));
        }
      },
    }),
    logout: builder.mutation<void, void>({
      query: () => ({
        url: apiPaths.auth.logout,
        credentials: 'include',
        method: 'POST',
      }),
      onQueryStarted: async (_, { dispatch, queryFulfilled }) => {
        try {
          await queryFulfilled;
          dispatch(logout());
        } catch (error) {
          dispatch(logout());
        }
      },
    }),
  }),
});

export const {
  useLoginMutation,
  useRegisterMutation,
  useRefreshTokenMutation,
  useLogoutMutation,
} = authApi;
