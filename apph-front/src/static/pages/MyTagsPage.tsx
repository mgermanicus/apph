import React, { useEffect, useState } from 'react';
import ReactWordcloud, { Scale, Spiral, Word } from 'react-wordcloud';
import TagService from '../../services/TagService';
import { AlertSnackbar } from '../components/AlertSnackbar';

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

  const options = {
    enableTooltip: true,
    deterministic: true,
    fontFamily: 'impact',
    fontSizes: [20, 100] as [number, number],
    fontStyle: 'normal',
    fontWeight: 'normal',
    padding: 2,
    rotations: 0,
    scale: 'sqrt' as Scale,
    spiral: 'archimedean' as Spiral,
    transitionDuration: 1000
  };

  return (
    <div>
      <div style={{ width: '100%', height: '100%' }}>
        <ReactWordcloud options={options} words={tags} />
      </div>
      <AlertSnackbar
        open={!!errorMessage}
        severity={'warning'}
        message={errorMessage}
        onClose={() => setErrorMessage('')}
      />
    </div>
  );
};
