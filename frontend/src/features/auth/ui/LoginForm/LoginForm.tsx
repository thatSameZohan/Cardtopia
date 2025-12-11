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
import { useLoginMutation } from '@/redux/auth/auth';
import { useRouter, useSearchParams } from 'next/navigation';

export function LoginForm() {
  const [login, { isLoading }] = useLoginMutation();
  const router = useRouter();
  const searchParams = useSearchParams();
  const { handleSubmit, control } = useForm<LoginInput>({
    resolver: zodResolver(loginSchema),
    defaultValues: { username: '', password: '' },
  });

  const submit = async (formData: LoginInput) => {
    try {
      await login({ username: formData.username, password: formData.password }).unwrap();
      // const returnTo = searchParams.get('returnTo');
      //  router.push(returnTo || routes.homepage);
      router.push(routes.homepage);
    } catch (error) {
      // Ошибка обработается в RTK Query
    }
  };

  return (
    <AuthWrapper title="Вход в аккаунт" subtitle="Введите логин и пароль для входа">
      <form onSubmit={handleSubmit(submit)}>
        <FormInput control={control} type="text" name="username" label="Логин" placeholder="Введите логин" />
        <FormPassword control={control} name="password" label="Пароль" placeholder="Введите пароль" />
        <Button loading={isLoading} fullWidth variant="glow">
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
