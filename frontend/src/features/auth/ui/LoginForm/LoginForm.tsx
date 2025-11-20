'use client';

import { Controller, useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { Button, Checkbox, Group, Paper, Text, Title } from '@mantine/core';

import { type LoginInput, loginSchema } from 'features/auth/model/auth';
import { routes } from 'shared/router/paths';
import { FormInput, FormPassword } from 'shared/ui/Inputs';

import styles from './LoginForm.module.scss';

export function LoginForm() {
  const {
    handleSubmit,
    control,
    formState: { isSubmitting, isDirty },
  } = useForm<LoginInput>({
    resolver: zodResolver(loginSchema),
    defaultValues: { email: '', password: '', remember: false },
  });

  const submit = (data: LoginInput) => {
    console.log('Submitted', data);
  };

  return (
    <Paper withBorder radius="md" p="xl" className={styles.container}>
      <Title order={2}>Вход в аккаунт</Title>
      <Text size="sm">Введите email и пароль для входа</Text>

      <form className={styles.form} onSubmit={handleSubmit(submit)}>
        <FormInput control={control} name="email" label="Email" placeholder="you@example.com" />
        <FormPassword control={control} name="password" label="Пароль" placeholder="••••••••" />

        <Controller
          name="remember"
          control={control}
          render={({ field }) => (
            <Checkbox
              ref={field.ref}
              label="Запомнить меня"
              name={field.name}
              checked={field.value}
              onBlur={field.onBlur}
              onChange={(event) => {
                field.onChange(event.currentTarget.checked);
              }}
            />
          )}
        />

        <Button fullWidth type="submit" size="xl" radius="md" loading={isSubmitting} disabled={!isDirty}>
          Войти
        </Button>

        <Group justify="space-between" mt="md">
          <a href="/forgot" className={styles.link}>
            Забыли пароль?
          </a>
          <a href={routes.register} className={styles.link}>
            Регистрация
          </a>
        </Group>
      </form>
    </Paper>
  );
}
