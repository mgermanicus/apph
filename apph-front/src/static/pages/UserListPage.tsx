import { useUserTable } from '../../utils/hooks/useUserTable';
import { AlertSnackbar } from '../components/AlertSnackbar';
import * as React from 'react';

export const UserListPage = (): JSX.Element => {
  const { errorState, userTable } = useUserTable();

  return (
    <>
      {userTable}
      <AlertSnackbar
        open={!!errorState.getMessage}
        severity={'warning'}
        message={errorState.getMessage}
        onClose={() => errorState.setMessage('')}
      />
    </>
  );
};
