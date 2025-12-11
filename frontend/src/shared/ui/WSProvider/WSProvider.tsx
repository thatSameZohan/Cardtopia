'use client';
import { useWS } from '@/shared/hook/useWS';
import React, { createContext, ReactNode, useContext } from 'react';
type WSContextType = ReturnType<typeof useWS>;
const WSContext = createContext<WSContextType | null>(null);

export const WSProvider = ({ children }: { children: ReactNode }) => {
  const ws = useWS();

  return <WSContext.Provider value={ws}>{children}</WSContext.Provider>;
};
export const useWSContext = () => {
  const ctx = useContext(WSContext);
  if (!ctx) throw new Error('useWSContext должен использоваться только в WSProvider');
  return ctx;
};
