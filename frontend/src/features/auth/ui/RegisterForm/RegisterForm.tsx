'use client';

import { Controller, useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import Link from 'next/link';

import { type RegisterInput, registerSchema } from '@/features/auth/model/auth';
import { routes } from '@/shared/router/paths';
import { FormInput, FormPassword } from '@/shared/ui/Inputs';
import { Button } from '@/shared/ui/Button';
import { Checkbox } from '@/shared/ui/Checkbox';
import { AuthWrapper } from '@/shared/ui/AuthWrapper';
import styles from '@/shared/ui/AuthWrapper/AuthWrapper.module.scss';

import { useRegisterMutation, useLoginMutation } from '@/redux/auth/auth';
import { useRouter } from 'next/navigation';

export function RegisterForm() { 

  const router = useRouter();

  const [register] = useRegisterMutation();
  const [login] = useLoginMutation();
  
  const {
    handleSubmit,
    control,
    formState: { isSubmitting, isDirty, isValid },
  } = useForm<RegisterInput>({
    resolver: zodResolver(registerSchema),
    defaultValues: { login: '', password: '',agreeToTerms: false },
  });


  const submit = async (formData: RegisterInput) => {
    try {
     await register(formData).unwrap();
      await login({ login: formData.login, password: formData.password }).unwrap();
       router.push(routes.homepage);
    } catch (error) { 
    }
  };

  return (
    <AuthWrapper title="Регистрация" subtitle="Создайте новый аккаунт">
      <form onSubmit={handleSubmit(submit)}>
        <FormInput required control={control} name="login" label="Логин" placeholder="Введите логин" />
        <FormPassword required control={control} name="password" label="Пароль" placeholder="Введите пароль" />

        <Controller
          name="agreeToTerms"
          control={control}
          render={({ field, fieldState: { error } }) => (
            <Checkbox
              ref={field.ref}
              name={field.name}
              onBlur={field.onBlur}
              label="Я согласен с условиями использования"
              checked={field.value}
              onChange={(e: React.ChangeEvent<HTMLInputElement>) => {
                field.onChange(e.target.checked);
              }}
              error={error?.message}
            />
          )}
        />

        <Button fullWidth type="submit" loading={isSubmitting} disabled={!isDirty || !isValid} variant="glow">
          Зарегистрироваться
        </Button>

        <div className={styles.linksContainer}>
          <Link href={routes.login} className={styles.link}>
            Уже есть аккаунт?
          </Link>
        </div>
      </form>
    </AuthWrapper>
  );
}
