import 'styles/app.css';
import 'styles/variables.css';
import 'styles/normalize.css';

import { type ReactNode } from 'react';

export default function RootLayout({ children }: { children: ReactNode }) {
  return (
    <html lang="en">
      <body>{children}</body>
    </html>
  );
}
