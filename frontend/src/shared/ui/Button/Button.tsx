import { type ButtonHTMLAttributes, type ReactNode } from 'react';
import cn from 'clsx';
import styles from './Button.module.scss';

type ButtonProps = {
  children: ReactNode;
  variant?: 'glow' | 'dark';
  loading?: boolean;
  fullWidth?: boolean;
} & ButtonHTMLAttributes<HTMLButtonElement>;

export function Button({ children, className, variant = 'glow', loading, fullWidth, ...props }: ButtonProps) {
  return (
    <button
      className={cn(styles.button, variant && styles[variant], fullWidth && styles.fullWidth, className)}
      disabled={loading || props.disabled}
      {...props}
    >
      {loading ? <span className={styles.loader} /> : <span className={styles.label}>{children}</span>}
    </button>
  );
}
