'use client';
import '@/app/styles/global.scss';
import '@/app/styles/variables.scss';
import '@/app/styles/normalize.css';

import { type ReactNode } from 'react';

export default function RootLayout({ children }: { children: ReactNode }) {
  return (
    <html lang="en">
      <head>
        <link rel="preconnect" href="https://fonts.googleapis.com" />
        <link rel="preconnect" href="https://fonts.gstatic.com" crossOrigin="anonymous" />
        <link href="https://fonts.googleapis.com/css2?family=Roboto:wght@400;500;700&display=swap" rel="stylesheet" />
      </head>
      <body>
        <h1>Привет, мир!</h1>
        {children}
      </body>
    </html>
  );
}
