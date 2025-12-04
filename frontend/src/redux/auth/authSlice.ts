import { createSlice, PayloadAction } from '@reduxjs/toolkit';

export interface AuthState {
  accessToken: string | null;
  isAuth: boolean;
  isInitialLoad: boolean; // Добавлено для отслеживания начальной загрузки
  username: string | null;
}

const initialState: AuthState = {
  accessToken: null,
  isAuth: false,
  isInitialLoad: true, // Изначально true, пока не проверим сессию
  username: null,
};

const authSlice = createSlice({
  name: 'auth',
  initialState,
  reducers: {
    setTokens(state, action: PayloadAction<{ accessToken: string; username?: string }>) {
      state.accessToken = action.payload.accessToken;
      if (action.payload.username) state.username = action.payload.username;
      state.isAuth = true;
      state.isInitialLoad = false;
    },
    logout: {
      reducer(state) {
        state.accessToken = null;
        state.username = null;
        state.isAuth = false;
        state.isInitialLoad = false;
      },
      prepare(payload: { noRedirect?: boolean; noRedirectLink?: boolean } = {}) {
        return { payload };
      },
    },
    setInitialLoad(state, action: PayloadAction<boolean>) {
      state.isInitialLoad = action.payload;
    },
  },
});

export const { setTokens, logout, setInitialLoad } = authSlice.actions;
export default authSlice.reducer;
