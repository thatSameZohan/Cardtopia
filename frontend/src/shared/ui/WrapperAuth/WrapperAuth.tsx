import styles from './WrapperAuth.module.scss';
export function WrapperAuth({ children }: { children: React.ReactNode }) {
  return (
    <section className={styles.root}>
      <video autoPlay muted loop src="/assets/video/fon2.mp4" className={styles.fon} />
      {children}
    </section>
  );
}
