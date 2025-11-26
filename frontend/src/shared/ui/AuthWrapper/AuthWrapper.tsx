import { type ReactNode } from 'react';
import styles from './AuthWrapper.module.scss';

type AuthWrapperProps = {
  children: ReactNode;
  title: string;
  subtitle: string;
};

export function AuthWrapper({ children, title, subtitle }: AuthWrapperProps) {
  return (
    <div className={styles.container}>
      <div className={styles.header}>
        <h2 className={styles.title}>{title}</h2>
        <p className={styles.subtitle}>{subtitle}</p>
      </div>
      {children}
    </div>
  );
}
