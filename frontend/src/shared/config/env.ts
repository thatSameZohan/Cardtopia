import { z } from 'zod';

export const siteUrl = z
  .string()
  .url('Provided siteUrl env variable is not a url')
  .parse(process.env.NEXT_PUBLIC_SITE_URL);
