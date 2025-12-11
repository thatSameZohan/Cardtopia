'use client';

import { type Control, type FieldValues, type Path, useController, type UseControllerProps } from 'react-hook-form';
import cn from 'clsx';
import styles from './Inputs.module.scss';
import { useId } from 'react';

export type FormInputProps<T extends FieldValues> = {
  name: Path<T>;
  control: Control<T>;
  label: string;
  type?: 'text' | 'email' | 'tel' | 'password' | 'number'; // ← добавлено
  placeholder?: string;
  required?: boolean | string;
} & UseControllerProps<T>;

export function FormInput<T extends FieldValues>({
  name,
  control,
  label,
  type = 'text', // ← значение по умолчанию
  placeholder,
  rules,
  defaultValue,
  required,
}: FormInputProps<T>) {
  const { field, fieldState } = useController({
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

      <input
        id={id}
        type={type} // ← применён тип
        placeholder={placeholder}
        className={cn(styles.input, fieldState.error && styles.inputErrorState)}
        {...field}
      />

      {fieldState.error && <p className={styles.inputError}>{fieldState.error.message}</p>}
    </div>
  );
}
