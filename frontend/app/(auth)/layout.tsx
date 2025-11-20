import { type ReactNode } from 'react';

import { WrapperAuth } from 'shared/ui/WrapperAuth';

export default function AuthLayout({ children }: { children: ReactNode }) {
  return <WrapperAuth>{children}</WrapperAuth>;
}
