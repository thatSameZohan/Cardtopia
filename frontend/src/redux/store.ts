import { configureStore } from '@reduxjs/toolkit';

import { api } from './api';
import authReducer from './auth/authSlice';
import gameReducer from './gameSlice/gameSlice';
import { rtkQueryErrorLogger } from './rtkQueryErrorLogger';
import { stompMiddleware } from './gameSlice/stompMiddleware';

export const store = configureStore({
  reducer: {
    [api.reducerPath]: api.reducer,
    auth: authReducer,
    game: gameReducer,
  },
  middleware: (getDefaultMiddleware) =>
    getDefaultMiddleware().concat(api.middleware, rtkQueryErrorLogger, stompMiddleware),
});

export type RootState = ReturnType<typeof store.getState>;
export type AppDispatch = typeof store.dispatch;
