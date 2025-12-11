import { type SitemapProps } from './sitemap.types';

export const sitemapMock = [
  {
    url: '/',
    lastModified: new Date(),
    priority: 1,
  },
] satisfies SitemapProps;
