'use client';

import { Controller, useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { Button, Checkbox, Group, Paper, Text, Title } from '@mantine/core';

import { type RegisterInput, registerSchema } from 'features/auth/model/auth';
import { routes } from 'shared/router/paths';
import { FormInput, FormPassword } from 'shared/ui/Inputs';

import styles from './RegisterForm.module.scss';

export function RegisterForm() {
  const {
    handleSubmit,
    control,
    formState: { isSubmitting, isDirty },
  } = useForm<RegisterInput>({
    resolver: zodResolver(registerSchema),
    defaultValues: { name: '', email: '', password: '', confirmPassword: '', agreeToTerms: false },
  });

  const submit = (data: RegisterInput) => {
    console.log('Submitted', data);
  };

  return (
    <Paper withBorder radius="md" p="xl" className={styles.container}>
      <Title order={2}>Регистрация</Title>
      <Text size="sm">Создайте новый аккаунт</Text>

      <form className={styles.form} onSubmit={handleSubmit(submit)}>
        <FormInput required control={control} name="name" label="Имя" placeholder="Ваше имя" />
        <FormInput required control={control} name="email" label="Email" placeholder="you@example.com" />
        <FormPassword required control={control} name="password" label="Пароль" placeholder="••••••••" />
        <FormPassword
          required
          control={control}
          name="confirmPassword"
          label="Подтвердите пароль"
          placeholder="••••••••"
        />

        <Controller
          name="agreeToTerms"
          control={control}
          render={({ field }) => (
            <Checkbox
              ref={field.ref}
              label="Я согласен с условиями использования"
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
          Зарегистрироваться
        </Button>

        <Group justify="center" mt="md">
          <a href={routes.login} className={styles.link}>
            Уже есть аккаунт?
          </a>
        </Group>
      </form>
    </Paper>
  );
}
