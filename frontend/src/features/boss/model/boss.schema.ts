import { z } from 'zod';

export const BossSchema = z.object({
  health: z.number().min(0),
});

export type BossType = z.infer<typeof BossSchema>;
