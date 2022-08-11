import { IconButton } from '@mui/material';
import { Delete } from '@mui/icons-material';
import UserService from '../../services/UserService';
import { useState } from 'react';
import { ConfirmationDialog } from './ConfirmationDialog';
import { t } from 'i18next';

export const DeleteUser = ({
  email,
  setRefresh
}: {
  email: string;
  setRefresh: React.Dispatch<React.SetStateAction<boolean>>;
}) => {
  const [dialogOpen, setDialogOpen] = useState<boolean>(false);
  const deleteUser = () =>
    UserService.deleteUser(
      email,
      () => setRefresh((refresh) => !refresh),
      (error) => console.log(error)
    );
  return (
    <>
      <IconButton onClick={() => setDialogOpen(true)}>
        <Delete />
      </IconButton>
      <ConfirmationDialog
        open={dialogOpen}
        title={t('userTable.confirmDelete')}
        onConfirm={deleteUser}
        onCancel={() => setDialogOpen(false)}
      />
    </>
  );
};
