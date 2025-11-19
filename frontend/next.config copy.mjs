/** @type {import('next').NextConfig} */
const config = {
  poweredByHeader: false,
  reactStrictMode: false,
  output: 'standalone',
  logging: {
    fetches: {
      fullUrl: true,
    },
  },
  eslint: {
    ignoreDuringBuilds: true,
  },
  async redirects() {
    return [
      {
        source: '/dashboard',
        destination: '/dashboard/events',
        permanent: true,
      },
      {
        source: '/email-confirm/:token',
        destination: '/email/confirm/:token',
        permanent: true,
      },
    ];
  },
  experimental: {
    scrollRestoration: false,
  },
  images: {
    qualities: [75, 90, 100],
    remotePatterns: [
      {
        protocol: 'https',
        hostname: 'storage.yandexcloud.net',
      },
      {
        protocol: 'https',
        hostname: 'ecosystem-upload.storage.yandexcloud.net',
      },
      {
        protocol: 'https',
        hostname: 'ecosystem.afedorov.brotherhood.software',
      },
      {
        protocol: 'https',
        hostname: 'esinfra.ru',
      },
      {
        protocol: 'https',
        hostname: 'edu.dobro.ru',
      },
      {
        protocol: 'https',
        hostname: 'ecosystem-upload.storage.yandexcloud.net',
      },
      {
        protocol: 'https',
        hostname: 'images.unsplash.com',
      },
      { protocol: 'https', hostname: 'yastatic.net' },
    ],
  },
  // eslint-disable-next-line no-shadow
  webpack(config) {
    // Grab the existing rule that handles SVG imports
    const fileLoaderRule = config.module.rules.find((rule) => rule.test?.test?.('.svg'));

    config.module.rules.push(
      // Reapply the existing rule, but only for svg imports ending in ?url
      {
        ...fileLoaderRule,
        test: /\.svg$/i,
        resourceQuery: /url/, // *.svg?url
      },
      // Convert all other *.svg imports to React components
      {
        test: /\.svg$/i,
        issuer: fileLoaderRule.issuer,
        resourceQuery: { not: [...fileLoaderRule.resourceQuery.not, /url/] }, // exclude if *.svg?url
        use: ['@svgr/webpack'],
      },
    );

    // Modify the file loader rule to ignore *.svg, since we have it handled now.
    fileLoaderRule.exclude = /\.svg$/i;

    return config;
  },
};

export default config;
