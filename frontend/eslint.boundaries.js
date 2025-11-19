import boundaries from 'eslint-plugin-boundaries';

const eslintBoundariesConfig = {
  plugins: {
    boundaries,
  },
  settings: {
    'import/resolver': {
      typescript: {
        alwaysTryTypes: true,
      },
    },

    'boundaries/elements': [
      { type: 'app', pattern: 'app/*' },
      { type: 'pages', pattern: 'pages/*' },
      { type: 'features', pattern: 'features/*' },
      { type: 'services', pattern: 'services/*' },
      { type: 'shared', pattern: 'shared/*' },
      { type: 'redux', pattern: 'redux/*' },
    ],
  },
  rules: {
    'boundaries/element-types': [
      2,
      {
        default: 'allow',
        message:
          // eslint-disable-next-line no-template-curly-in-string
          'Модуль нижележащего слоя (${file.type}) не может импортировать модуль вышележащего слоя (${dependency.type})',
        rules: [
          // Импорт из выжележащих слоев

          // Запрещаем импорты в pages из app
          {
            from: 'pages',
            disallow: ['app'],
          },
          // Запрещаем импорты в features из app, pages
          {
            from: 'features',
            disallow: ['app', 'pages'],
          },
          // Запрещаем импорты в services из app, pages, features
          {
            from: 'services',
            disallow: ['app', 'pages', 'features'],
          },
          // Запрещаем импорты в shared из других слоев
          {
            from: 'shared',
            disallow: ['app', 'pages', 'features', 'services'],
          },

          // Кросс-импорт

          // {
          //   from: 'pages',
          //   disallow: 'pages',
          //   message: 'Модуль не может импортирован из того же слоя',
          // },
          // {
          //   from: 'features',
          //   disallow: 'features',
          //   message: 'Модуль не может импортирован из того же слоя',
          // },
          // {
          //   from: 'services',
          //   disallow: 'services',
          //   message: 'Модуль не может импортирован из того же слоя',
          // },
        ],
      },
    ],

    'boundaries/entry-point': [
      2,
      {
        default: 'disallow',
        message: 'Модуль должен импортироваться через Public API (index.ts). Прямой импорт запрещен',

        rules: [
          {
            target: ['features', 'services', 'pages'],
            allow: '**/index.(ts|tsx)',
          },
          {
            target: ['shared', 'app', 'redux'],
            allow: '**',
          },
        ],
      },
    ],
  },
};

export default eslintBoundariesConfig;
