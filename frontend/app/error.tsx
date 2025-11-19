'use client';

import { useEffect } from 'react';

export default function ErrorPage({ error, reset }: { error: Error; reset: () => void }) {
  useEffect(() => {
    // Log the error to an error reporting service
    console.error(error);
  }, [error]);

  return (
    <>
      <h1>Something went wrong!</h1>
      <button
        type="button"
        onClick={
          // Attempt to recover by trying to re-render the segment
          () => {
            reset();
          }
        }
      >
        Try again
      </button>
    </>
  );
}
