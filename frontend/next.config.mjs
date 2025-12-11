import path from 'path';

/** @type {import('next').NextConfig} */
const config = {
  poweredByHeader: false,
  reactStrictMode: true,
  sassOptions: {
    includePaths: [path.join(process.cwd(), 'src', 'app', 'styles')],
  },
};

export default config;
