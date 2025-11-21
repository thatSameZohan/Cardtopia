import { type MetadataRoute } from 'next';
import { sitemapApi } from 'app/sitemap/sitemap.fetch';
import { siteUrl } from 'shared/config/env';

export default async function sitemap(): Promise<MetadataRoute.Sitemap> {
  const data = await sitemapApi.get();
  return data.map(({ url, lastModified, priority }) => ({
    url: siteUrl + url,
    lastModified,
    priority,
  }));
}
