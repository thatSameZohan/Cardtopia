import path from 'path';
import { fileURLToPath } from 'url';

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);

/** @type {import('next').NextConfig} */
const config = {
  poweredByHeader: false,
  reactStrictMode: true,
  output: 'standalone',

  sassOptions: {
    includePaths: [path.join(__dirname, 'src', 'app', 'styles')],
  },

  turbopack: {},

  async rewrites() {
    const apiUrl =
      process.env.NEXT_PUBLIC_API_URL_SERVER || 'http://backend:8080';
    return [
      { source: '/api/:path*', destination: `${apiUrl}/api/:path*` },
      { source: '/ws', destination: `${apiUrl}/ws` },
    ];
  },
};

export default config;
