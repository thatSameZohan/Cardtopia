import { type MetadataRoute } from 'next';
import { z } from 'zod';

export const schemaSitemap = z.custom<MetadataRoute.Sitemap>();

export type SitemapProps = z.infer<typeof schemaSitemap>;
