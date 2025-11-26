'use client';

import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import Link from 'next/link';

import { type LoginInput, loginSchema } from 'features/auth/model/auth';
import { routes } from 'shared/router/paths';
import { FormInput, FormPassword } from 'shared/ui/Inputs';
import { Button } from '@/shared/ui/Button';
import { AuthWrapper } from '@/shared/ui/AuthWrapper';
import styles from '@/shared/ui/AuthWrapper/AuthWrapper.module.scss';

export function LoginForm() {
  const {
    handleSubmit,
    control,
    formState: { isSubmitting, isDirty },
  } = useForm<LoginInput>({
    resolver: zodResolver(loginSchema),
    defaultValues: { email: '', password: '' },
  });

  const submit = (data: LoginInput) => {
    console.log('Submitted', data);
  };

  return (
    <AuthWrapper title="Вход в аккаунт" subtitle="Введите email и пароль для входа">
      <form onSubmit={handleSubmit(submit)}>
        <FormInput control={control} name="email" label="Email" placeholder="you@example.com" />
        <FormPassword control={control} name="password" label="Пароль" placeholder="••••••••" />
        <Button loading={isSubmitting} disabled={!isDirty} fullWidth variant="glow">
          Войти
        </Button>
        <div className={styles.linksContainer}>
          <Link href="/forgot" className={styles.link}>
            Забыли пароль?
          </Link>
          <Link href={routes.register} className={styles.link}>
            Регистрация
          </Link>
        </div>
      </form>
    </AuthWrapper>
  );
}
