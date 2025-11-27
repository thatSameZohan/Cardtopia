import { z } from 'zod';

export const loginSchema = z.object({
  login: z.string().email({ message: 'Некорректный email' }),
  password: z.string().min(6, { message: 'Минимум 6 символов' }),
});

export type LoginInput = z.infer<typeof loginSchema>;

export const registerSchema = z
  .object({
   
    login: z.string().email({ message: 'Некорректный email' }),
    password: z.string().min(6, { message: 'Минимум 6 символов' }),
    agreeToTerms: z.boolean().refine((v) => v, { message: 'Необходимо согласие с условиями' }),
  })
 
  
export type RegisterInput = z.infer<typeof registerSchema>;
