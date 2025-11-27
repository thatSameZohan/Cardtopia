import { createSlice, PayloadAction } from '@reduxjs/toolkit';
import Cookies from 'js-cookie';

export interface AuthState {
  accessToken: string | null;
  isAuth: boolean;
  loading: boolean; // новое поле
}

const initialState: AuthState = {
  accessToken: null,
  isAuth: false,
  loading: true, // пока проверяем токен
};

const authSlice = createSlice({
  name: 'auth',
  initialState,
  reducers: {
    setTokens(state, action: PayloadAction<{ accessToken: string; refreshToken: string }>) {
      state.accessToken = action.payload.accessToken;
      Cookies.set('access_token', action.payload.accessToken, { expires: 7 }); // Добавляем сохранение access_token в куки
      Cookies.set('refresh_token', action.payload.refreshToken, { expires: 7 });
      state.isAuth = true;
      state.loading = false;
    },
    logout(state, action: PayloadAction<{ noRedirect?: boolean } | undefined>) {
      state.accessToken = null;
      state.isAuth = false;
      state.loading = false;
      Cookies.remove('access_token'); // Удаляем access_token из куки
      Cookies.remove('refresh_token');
      if (!action?.payload?.noRedirect) {
        // Здесь можно добавить логику перенаправления, если это необходимо
        // Например: window.location.href = '/login';
      }
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
