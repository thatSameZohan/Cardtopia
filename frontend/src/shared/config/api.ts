export const BASE_URL =
  typeof window === 'undefined' ? process.env.NEXT_PUBLIC_API_URL_SERVER : process.env.NEXT_PUBLIC_API_URL;
