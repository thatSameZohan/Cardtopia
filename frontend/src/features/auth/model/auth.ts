import { z } from 'zod';

export const loginSchema = z.object({
  email: z.string().email({ message: 'Некорректный email' }),
  password: z.string().min(6, { message: 'Минимум 6 символов' }),
  remember: z.boolean().optional(),
});

export type LoginInput = z.infer<typeof loginSchema>;

export const registerSchema = z
  .object({
    name: z.string().min(2, { message: 'Введите имя (минимум 2 символа)' }),
    email: z.string().email({ message: 'Некорректный email' }),
    password: z.string().min(6, { message: 'Минимум 6 символов' }),
    confirmPassword: z.string().min(6, { message: 'Повторите пароль' }),
    agreeToTerms: z.boolean().refine((v) => v, { message: 'Необходимо согласие с условиями' }),
  })
  .refine((data) => data.password === data.confirmPassword, {
    message: 'Пароли не совпадают',
    path: ['confirmPassword'],
  });

import { BASE_URL } from '@/shared/config/api';

export type RegisterInput = z.infer<typeof registerSchema>;

export interface RegisterInput1 {
  login: string;
  password: string;
  authority: string;
}
export const registr = async (data: RegisterInput1) => {
  const response = await fetch(`${BASE_URL}/v1/api/auth/register`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(data),
  });

  return response.json();
};
