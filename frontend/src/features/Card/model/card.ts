import { z } from 'zod';

export const CardType = z.object({
  name: z.string().min(1),
  attack: z.number().int().min(0),
  cost: z.number().int().min(0),
});

export type CardType = z.infer<typeof CardType>;
