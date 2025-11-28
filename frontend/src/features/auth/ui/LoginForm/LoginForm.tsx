'use client';

import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import Link from 'next/link';
import { useSelector } from 'react-redux'; // Импортируем useSelector
import { RootState } from '@/redux/store'; // Импортируем RootState

import { type LoginInput, loginSchema } from 'features/auth/model/auth';
import { routes } from 'shared/router/paths';
import { FormInput, FormPassword } from 'shared/ui/Inputs';
import { Button } from '@/shared/ui/Button';
import { AuthWrapper } from '@/shared/ui/AuthWrapper';
import styles from '@/shared/ui/AuthWrapper/AuthWrapper.module.scss';
import { useLoginMutation } from '@/redux/auth/auth';
import { useRouter } from 'next/navigation';

export function LoginForm() {

  const [login] = useLoginMutation();
  const router = useRouter();
  const {
    handleSubmit,
    control,
    formState: { isSubmitting, isDirty, isValid },
  } = useForm<LoginInput>({
    resolver: zodResolver(loginSchema),
    defaultValues: { login: '', password: '' },
  });


  const submit = async (formData: LoginInput) => {
    try {
      const result = await login({ login: formData.login, password: formData.password }).unwrap();
      router.push(routes.homepage);
    } catch (error) {
      // Ошибка будет обработана в middleware
    }
  };

  return (
    <AuthWrapper title="Вход в аккаунт" subtitle="Введите email и пароль для входа">
      <form onSubmit={handleSubmit(submit)}>
        <FormInput control={control} name="login" label="Email" placeholder="you@example.com" />
        <FormPassword control={control} name="password" label="Пароль" placeholder="••••••••" />
        <Button loading={isSubmitting} disabled={!isDirty || !isValid} fullWidth variant="glow">
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
