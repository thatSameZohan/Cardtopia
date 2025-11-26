import { sitemapMock } from './sitemap.mock';
import { schemaSitemap } from './sitemap.types';

export const sitemapApi = {
  async get() {
    return schemaSitemap.parse(sitemapMock);
  },
};
