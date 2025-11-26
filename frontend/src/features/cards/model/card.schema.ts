import { z } from 'zod';

export const CardSchema = z.object({
  id: z.number(),
  name: z.string(),
  countAttack: z.number().min(0),
  used: z.boolean().optional(),
});

export type CardType = z.infer<typeof CardSchema>;
