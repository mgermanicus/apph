import React, { useEffect, useState } from 'react';
import { AlertSnackbar } from '../components/AlertSnackbar';
import { WordCloud } from '../components/WordCloud';
import TagService from '../../services/TagService';

export const MyTagsPage = (): JSX.Element => {
  const [tags, setTags] = useState<{ value: string; count: number }[]>([]);
  const [errorMessage, setErrorMessage] = useState<string>('');

  useEffect(() => {
    (async () => {
      await TagService.getAllTagsCount(
        (tags: { value: string; count: number }[]) => {
          setTags(tags);
        },
        (errorMessage: string) => {
          setErrorMessage(errorMessage);
        }
      );
    })();
  }, []);

  return (
    <div style={{ justifyContent: 'center' }}>
      <WordCloud tags={tags} />
      <AlertSnackbar
        open={!!errorMessage}
        severity={'warning'}
        message={errorMessage}
        onClose={() => setErrorMessage('')}
      />
    </div>
  );
};
