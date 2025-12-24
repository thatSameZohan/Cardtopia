import test from 'node:test';

export type ArgsRoute = (...args: (string | number)[]) => string;
export type Route = string | ArgsRoute;

export const routes = {
  homepage: '/',
  login: '/login',
  register: '/registration',
  test: '/test',
  room: (id) => `/room/${id}`,
  game: (id) => `/game/${id}`,
} satisfies Record<string, Route>;
