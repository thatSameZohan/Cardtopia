import { z } from 'zod';

export const PROD_ENV = process.env.NODE_ENV === 'production';

// URL для клиентской части (браузер)
export const SITE_URL =
  typeof window !== 'undefined'
    ? window.location.origin
    : z.string().url().safeParse(process.env.NEXT_PUBLIC_SITE_URL)?.data || 'http://localhost:3000';

// URL для API, используемый на клиенте
export const API_URL = z.string().url().safeParse(process.env.NEXT_PUBLIC_API_URL)?.data || 'http://localhost:8080';

// URL для API, используемый на сервере (SSR)
export const SSR_API_URL =
  z.string().url().safeParse(process.env.NEXT_PUBLIC_API_URL_SERVER)?.data || 'http://backend:8080';
