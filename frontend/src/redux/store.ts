import { configureStore } from '@reduxjs/toolkit';

import { api } from './api';
import authReducer from './auth/authSlice';
import { rtkQueryErrorLogger } from './rtkQueryErrorLogger';

export const store = configureStore({
  reducer: {
    [api.reducerPath]: api.reducer,
    auth: authReducer,
  },
  middleware: (getDefaultMiddleware) =>
    getDefaultMiddleware().concat(api.middleware, rtkQueryErrorLogger),
});

export type RootState = ReturnType<typeof store.getState>;
export type AppDispatch = typeof store.dispatch;
