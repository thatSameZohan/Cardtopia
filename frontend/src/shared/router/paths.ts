import test from "node:test";

export type ArgsRoute = (...args: (string | number)[]) => string;
export type Route = string | ArgsRoute;

export const routes = {
  // use typeHandle as a key if corresponding typeHandle exists
  homepage: '/',
  login: '/login',
  register: '/registration',
  test: '/test',
} satisfies Record<string, Route>;
