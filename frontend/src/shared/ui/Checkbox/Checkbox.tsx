'use client';

import { type InputHTMLAttributes, type ReactNode, useId, forwardRef } from 'react';
import cn from 'clsx';
import styles from './Checkbox.module.scss';

type CheckboxProps = {
  label?: ReactNode;
  error?: string;
} & Omit<InputHTMLAttributes<HTMLInputElement>, 'type'>;

export const Checkbox = forwardRef<HTMLInputElement, CheckboxProps>(({ label, className, error, ...props }, ref) => {
  const id = useId();

  return (
    <div className={styles.wrapper}>
      <div className={styles.container}>
        <input ref={ref} type="checkbox" id={id} className={cn(styles.checkbox, className)} {...props} />
        {label && (
          <label htmlFor={id} className={styles.label}>
            {label}
          </label>
        )}
      </div>
      {error && <p className={styles.errorText}>{error}</p>}
    </div>
  );
});

Checkbox.displayName = 'Checkbox';
