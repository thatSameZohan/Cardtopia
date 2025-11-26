'use client';

import { useController, type UseControllerProps, type FieldValues, type Path, type Control } from 'react-hook-form';
import { useId, useState } from 'react';
import cn from 'clsx';
import styles from './Inputs.module.scss';
import { EyeOpenIcon } from '@/shared/ui/icons/EyeOpenIcon';
import { EyeClosedIcon } from '@/shared/ui/icons/EyeClosedIcon';

// Простой хук для замены useDisclosure
const useToggle = (initialValue = false): [boolean, () => void] => {
  const [value, setValue] = useState(initialValue);
  const toggle = () => setValue((v) => !v);
  return [value, toggle];
};

export type FormPasswordProps<T extends FieldValues> = {
  name: Path<T>;
  control: Control<T>;
  label: string;
  placeholder?: string;
  required?: boolean | string;
} & UseControllerProps<T>;

export function FormPassword<T extends FieldValues>({
  name,
  control,
  label,
  placeholder,
  rules,
  defaultValue,
  required,
}: FormPasswordProps<T>) {
  const [visible, toggle] = useToggle(false);
  const {
    field,
    fieldState: { error },
  } = useController({
    name,
    control,
    rules: {
      ...rules,
      required: required ?? false,
    },
    defaultValue,
  });
  const id = useId();

  return (
    <div className={styles.inputWrapper}>
      <label htmlFor={id} className={styles.inputLabel}>
        {label}
        {required && <span className={styles.asterisk}> *</span>}
      </label>
      <div className={styles.passwordInputContainer}>
        <input
          id={id}
          type={visible ? 'text' : 'password'}
          placeholder={placeholder}
          className={cn(styles.input, error && styles.inputErrorState)}
          {...field}
        />
        <button type="button" onClick={toggle} className={styles.toggleButton}>
          {visible ? <EyeOpenIcon /> : <EyeClosedIcon />}
        </button>
      </div>
      {error && <p className={styles.inputError}>{error.message}</p>}
    </div>
  );
}
