import test from 'node:test';

export type ArgsRoute = (...args: (string | number)[]) => string;
export type Route = string | ArgsRoute;

export const routes = {
  homepage: '/',
  login: '/login',
  register: '/registration',
  test: '/test',
} satisfies Record<string, Route>;
