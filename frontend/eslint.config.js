import eslintNextPlugin from '@next/eslint-plugin-next';
import wizardryConfig from 'eslint-config-wizardry';
import eslintImportResolverTypescript from 'eslint-import-resolver-typescript';

import eslintBoundariesConfig from './eslint.boundaries.js';

const config = [
  ...wizardryConfig,
  eslintBoundariesConfig,
  {
    // Ограничиваем проверку только папками app и src
    files: ['app/**/*.{js,jsx,ts,tsx}', 'src/**/*.{js,jsx,ts,tsx}'],
    ignores: ['out/', 'dist/'],
    plugins: {
      '@next/next': eslintNextPlugin,
    },
    settings: {
      'import/resolver': {
        typescript: eslintImportResolverTypescript,
      },
    },
    rules: {
      'no-unused-vars': ['warn', { ignoreRestSiblings: true }],
      'no-param-reassign': [
        'error',
        {
          props: true,
          ignorePropertyModificationsFor: ['state'], // <- это разрешит state.value = ...
        },
      ],
      ...eslintNextPlugin.configs.recommended.rules,
      '@typescript-eslint/dot-notation': 'off',
      'simple-import-sort/imports': [
        'error',
        {
          groups: [
            ['^\\u0000', '^styles.+\\.s?css$'], // side effects
            ['^react', '^next', '^@?\\w', '^clsx'], // external packages
            ['^redux/'], // store folder
            ['^app', '^pages', '^features', '^services', '^shared'], // ED layers
            ['^@/', '^public'], // project root aliases
            ['^\\.\\.(?!/?$)', '^\\.\\./?$'], // parent imports
            ['^\\./(?=.*/)(?!/?$)', '^\\.(?!/?$)', '^\\./?$'], // relative imports
            ['^.+module\\.s?css$'], // styles
          ],
        },
      ],
      'react/require-default-props': [
        'error',
        {
          functions: 'defaultArguments',
          classes: 'defaultProps',
          ignoreFunctionalComponents: true,
        },
      ],
    },
  },
  {
    files: ['**/*Slice.ts'],
    rules: {
      'no-param-reassign': ['error', { props: true, ignorePropertyModificationsFor: ['state'] }],
    },
  },
  {
    files: ['**/*Api.ts', '**/api.ts'],
    rules: {
      '@typescript-eslint/no-invalid-void-type': 'off',
      '@typescript-eslint/no-explicit-any': 'off',
    },
  },
  {
    files: ['svgr.d.ts'],
    rules: {
      '@typescript-eslint/no-invalid-void-type': 'off',
      '@typescript-eslint/no-explicit-any': 'off',
    },
  },
];

export default config;
