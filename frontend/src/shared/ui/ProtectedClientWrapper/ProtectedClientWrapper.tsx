'use client';
import { Suspense, useEffect } from 'react';
import { useRestoreSession } from '@/shared/hook/useRestoreSession';
import { GlobalLoader } from '../GlobalLoader/GlobalLoader';

export function ProtectedClientWrapper({ children }: { children: React.ReactNode }) {
  useRestoreSession(); // безопасно, так как это клиент
  return <Suspense fallback={<GlobalLoader />}>{children}</Suspense>;
}
