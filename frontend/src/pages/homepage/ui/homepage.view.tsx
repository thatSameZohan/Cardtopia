import { Button } from '@mantine/core';

import styles from './homepage.module.scss';

export function HomepageView() {
  return (
    <main className={styles.main}>
      <h1>Hello world!</h1>
      ⌨️Happy coding🤓
      <span>version</span>
      <Button variant="default">Back</Button>
      <Button>Next step</Button>
    </main>
  );
}
