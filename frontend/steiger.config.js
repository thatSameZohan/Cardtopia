import fsd from '@feature-sliced/steiger-plugin';
import { defineConfig } from 'steiger';

const config = defineConfig([
  ...fsd.configs.recommended,
  {
    files: ['./src/**'],
    rules: {
      'fsd/insignificant-slice': 'off', // allow slices that have no references
    },
  },
  {
    files: ['./src/entities/**/server.ts'],
    rules: {
      'fsd/no-public-api-sidestep': 'off',
    },
  },
  {
    files: ['./src/shared/**'],
    rules: {
      'fsd/public-api': 'off',
      'fsd/no-public-api-sidestep': 'off',
      'fsd/no-reserved-folder-names': 'off', // allow lib/model/ui segments
    },
  },
]);

export default config;
