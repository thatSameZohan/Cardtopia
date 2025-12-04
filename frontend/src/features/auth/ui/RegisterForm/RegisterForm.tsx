'use client';

import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { useRouter } from 'next/navigation';
import { toast } from 'react-toastify';

import { RegisterInput, registerSchema } from '@/features/auth/model/auth';
import { routes } from '@/shared/router/paths';
import { useRegisterMutation, useLoginMutation } from '@/redux/auth/auth';
import { setTokens } from '@/redux/auth/authSlice';
import { AuthWrapper } from '@/shared/ui/AuthWrapper';
import { FormInput, FormPassword } from '@/shared/ui/Inputs';
import { Button } from '@/shared/ui/Button';
import styles from '@/shared/ui/AuthWrapper/AuthWrapper.module.scss';

export function RegisterForm() {
  const router = useRouter();
  const [register] = useRegisterMutation();
  const [login, { isLoading: isLoggingIn }] = useLoginMutation();

  const { handleSubmit, control, formState } = useForm<RegisterInput>({
    resolver: zodResolver(registerSchema),
    defaultValues: { username: '', password: '' },
  });

  const submit = async (formData: RegisterInput) => {
    try {
      await register(formData).unwrap();
      const loginRes = await login({ username: formData.username, password: formData.password }).unwrap();

      // Сохраняем токен в Redux
      setTokens({ accessToken: loginRes.accessToken });

      toast.success('Вы успешно зарегистрированы и вошли в систему');
      router.push(routes.homepage);
    } catch {
      toast.error('Ошибка регистрации или входа');
    }
  };

  return (
    <AuthWrapper title="Регистрация" subtitle="Создайте новый аккаунт">
      <form onSubmit={handleSubmit(submit)}>
        <FormInput required control={control} name="username" label="Логин" placeholder="Введите логин" />
        <FormPassword required control={control} name="password" label="Пароль" placeholder="Введите пароль" />
        <Button
          fullWidth
          type="submit"
          loading={isLoggingIn}
          disabled={!formState.isDirty || !formState.isValid}
          variant="glow"
        >
          Зарегистрироваться
        </Button>
        <div className={styles.linksContainer}>
          <a href={routes.login} className={styles.link}>
            Уже есть аккаунт?
          </a>
        </div>
      </form>
    </AuthWrapper>
  );
}
