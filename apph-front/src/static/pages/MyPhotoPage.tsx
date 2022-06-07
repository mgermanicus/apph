import * as React from 'react';
import { AlertSnackbar } from '../components/AlertSnackbar';
import { usePhotoTable } from '../../utils/hooks/usePhotoTable';

export const MyPhotoPage = (): JSX.Element => {
  const { errorState, photoTable } = usePhotoTable();

  return (
    <>
      {photoTable}
      <AlertSnackbar
        open={!!errorState.getMessage}
        severity={'warning'}
        message={errorState.getMessage}
        onClose={() => errorState.setMessage('')}
      />
    </>
  );
};
