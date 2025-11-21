import '@mantine/core/styles.css';
import 'styles/app.css';
import 'styles/variables.css';
import 'styles/normalize.css';

import { type ReactNode } from 'react';
import { MantineProvider } from '@mantine/core';
export default function RootLayout({ children }: { children: ReactNode }) {
  return (
    <html lang="en">
      <body>
        <MantineProvider>{children}</MantineProvider>
      </body>
    </html>
  );
}
