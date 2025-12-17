'use client';
import { WSProvider } from '@/shared/ui/WSProvider';

export default function ProtectedLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  return <WSProvider>{children}</WSProvider>;
}
