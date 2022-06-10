import React, { useEffect, useState } from 'react';
import { AlertSnackbar } from '../components/AlertSnackbar';
import { TagCloud } from '../components/TagCloud';
import TagService from '../../services/TagService';
import { Word } from 'react-wordcloud';

export const MyTagsPage = (): JSX.Element => {
  const [tags, setTags] = useState<Word[]>([]);
  const [errorMessage, setErrorMessage] = useState<string>('');

  useEffect(() => {
    (async () => {
      await TagService.getAllTagsCount(
        (tags: Word[]) => {
          setTags(tags);
        },
        (errorMessage: string) => {
          setErrorMessage(errorMessage);
        }
      );
    })();
  }, []);

  return (
    <div>
      <TagCloud tags={tags} />
      <AlertSnackbar
        open={!!errorMessage}
        severity={'warning'}
        message={errorMessage}
        onClose={() => setErrorMessage('')}
      />
    </div>
  );
};
