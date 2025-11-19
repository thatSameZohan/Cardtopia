import { z } from 'zod';

export const schemaHomepage = z.object({
  version: z.string(),
});

export type HomepageApi = z.infer<typeof schemaHomepage>;
export type HomepageProps = Omit<HomepageApi, 'metadata'>;
