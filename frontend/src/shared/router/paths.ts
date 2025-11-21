export type ArgsRoute = (...args: (string | number)[]) => string;
export type Route = string | ArgsRoute;

export const routes = {
  // use typeHandle as a key if corresponding typeHandle exists
  homepage: '/',
  login: '/login',
  register: '/registration',
} satisfies Record<string, Route>;
