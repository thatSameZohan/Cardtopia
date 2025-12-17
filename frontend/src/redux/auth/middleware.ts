import { createListenerMiddleware } from '@reduxjs/toolkit';

import { logout } from './authSlice';
import { routes } from '@/shared/router/paths';

export const authMiddleware = createListenerMiddleware();

authMiddleware.startListening({
  actionCreator: logout,
  effect: (action) => {
    const defaultRoute = `${window.location.origin}${routes.login}`;
    const logoutRoute = action.payload?.noRedirectLink
      ? defaultRoute
      : `${defaultRoute}?redirect=${window.location.href}`;
    if (window?.location.href !== logoutRoute && !action.payload?.noRedirect)
      window.location.assign(logoutRoute);
  },
});
