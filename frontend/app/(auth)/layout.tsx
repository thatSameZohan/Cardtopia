import { WrapperAuth } from '@/shared/ui/WrapperAuth';
import { type ReactNode } from 'react';

export default function AuthLayout({ children }: { children: ReactNode }) {
  return <WrapperAuth>{children}</WrapperAuth>;
}
