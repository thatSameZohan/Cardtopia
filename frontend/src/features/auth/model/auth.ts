import { z } from 'zod';

export const loginSchema = z.object({
  username: z.string().min(2, { message: 'Минимум 2 символа' }),
  password: z.string().min(6, { message: 'Минимум 6 символов' }),
});

export type LoginInput = z.infer<typeof loginSchema>;

export const registerSchema = z.object({
  username: z.string().min(2, { message: 'Минимум 2 символа' }),
  password: z.string().min(6, { message: 'Минимум 6 символов' }),
});

export type RegisterInput = z.infer<typeof registerSchema>;
