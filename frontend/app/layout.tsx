'use client';
import 'react-toastify/dist/ReactToastify.css';
import '@/app/styles/global.scss';
import '@/app/styles/variables.scss';
import '@/app/styles/normalize.css';
import { Provider } from 'react-redux';
import { type ReactNode } from 'react';
import { ToastContainer } from 'react-toastify';
import { store } from '@/redux/store';



export default function RootLayout({ children }: { children: ReactNode }) {
  return (
    <html lang="en">
      <head>
        <link rel="preconnect" href="https://fonts.googleapis.com" />
        <link rel="preconnect" href="https://fonts.gstatic.com" crossOrigin="anonymous" />
        <link href="https://fonts.googleapis.com/css2?family=Roboto:wght@400;500;700&display=swap" rel="stylesheet" />
      </head>
      <body>
        <Provider store={store}>
          {children}
          <ToastContainer />
        </Provider>
      </body>
    </html>
  );
}
