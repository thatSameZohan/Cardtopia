import { type HomepageProps } from '../model/homepage.types';
import styles from './homepage.module.scss';

export function HomepageView({ version }: HomepageProps) {
  return (
    <main className={styles.main}>
      <h1>Hello world!</h1>
      ⌨️Happy coding🤓
      <span>{version}</span>
    </main>
  );
}
