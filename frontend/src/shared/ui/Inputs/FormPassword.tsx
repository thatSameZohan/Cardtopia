'use client';

import { type Control, type FieldValues, type Path, useController, type UseControllerProps } from 'react-hook-form';
import { InputWrapper, PasswordInput } from '@mantine/core';

import styles from './Inputs.module.scss';
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
  const { field, fieldState } = useController({
    name,
    control,
    rules: {
      ...rules,
      required: required ?? false,
    },
    defaultValue,
  });
  return (
    <InputWrapper
      label={label}
      error={fieldState.error?.message}
      classNames={{
        label: styles.inputLabel,
        error: styles.inputError,
      }}
      withAsterisk={!!required}
    >
      <PasswordInput placeholder={placeholder} size="xl" radius="md" {...field} />
    </InputWrapper>
  );
}
