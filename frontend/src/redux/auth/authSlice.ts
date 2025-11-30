import { createSlice, PayloadAction } from '@reduxjs/toolkit';
import Cookies from 'js-cookie';

export interface AuthState {
  accessToken: string | null;
  isAuth: boolean;
  loading: boolean; // новое поле
  userId: number | null;
}

const initialState: AuthState = {
  accessToken: null,
  isAuth: false,
  loading: true, // пока проверяем токен
  userId: null,
};

const authSlice = createSlice({
  name: 'auth',
  initialState,
  reducers: {
    setTokens(state, action: PayloadAction<{ accessToken: string; userId?: number }>) {
      state.accessToken = action.payload.accessToken;
      if (action.payload.userId) {
        state.userId = action.payload.userId;
      }
      Cookies.set('token', action.payload.accessToken, { expires: 7 });
      state.isAuth = true;
      state.loading = false;
    },
    logout(state, action: PayloadAction<{ noRedirect?: boolean } | undefined>) {
      state.accessToken = null;
      state.userId = null;
      state.isAuth = false;
      state.loading = false;
      Cookies.remove('token'); // Удаляем access_token из куки
      // Всегда перенаправляем после выхода
      window.location.href = '/login';
    },
    startLoading(state) {
      state.loading = true;
    },
    finishLoading(state) {
      state.loading = false;
    },
  },
});

export const { setTokens, logout, startLoading, finishLoading } = authSlice.actions;
export default authSlice.reducer;
