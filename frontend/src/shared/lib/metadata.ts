import { type Metadata } from 'next';

export const DEFAULT_TITLE = 'Карточная игра';
export const getMetadata = ({
  title,
  description,
  url,
  noIndex = false,
}: {
  url: string;
  noIndex?: boolean;
  title?: string;
  description?: string;
}): Metadata => {
  return {
    title: title ? `${title} | ${DEFAULT_TITLE}` : DEFAULT_TITLE,
    description: description || '',
    alternates: {
      canonical: url,
    },
    ...(noIndex && {
      robots: {
        index: false,
        follow: false,
      },
    }),
  };
};
