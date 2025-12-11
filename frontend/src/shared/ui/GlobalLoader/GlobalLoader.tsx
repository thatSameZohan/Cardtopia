import React from 'react';
import styles from './GlobalLoader.module.scss';

export const GlobalLoader = () => {
  return (
    <div className={styles.loaderWrapper}>
      <div className={styles.loader}></div>
    </div>
  );
};
