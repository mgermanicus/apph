import React, { Dispatch, SetStateAction, useState } from 'react';
import { AlertColor, Box, Button, Tooltip } from '@mui/material';
import { ConfirmationDialog } from './ConfirmationDialog';
import PhotoService from '../../services/PhotoService';
import { AlertSnackbar } from './AlertSnackbar';
import { Delete } from '@mui/icons-material';
import { useTranslation } from 'react-i18next';

export const DeleteImage = ({
  ids,
  setRefresh
}: {
  ids: number[];
  setRefresh?: Dispatch<SetStateAction<boolean>>;
}): JSX.Element => {
  const [message, setMessage] = useState('');
  const [dialogOpen, setDialogOpen] = useState<boolean>(false);
  const [snackbarOpen, setSnackbarOpen] = useState<boolean>(false);
  const [severity, setSeverity] = useState<AlertColor>();
  const { t } = useTranslation();

  const handleSubmit = () => {
    if (ids.length != 0) {
      setDialogOpen(true);
    } else {
      setSnackbarOpen(true);
      setMessage(t('photo.noneSelected'));
      setSeverity('warning');
    }
  };
  const deleteImage = async () => {
    await PhotoService.deleteImage(
      ids,
      (message) => {
        setMessage(message.message);
        setSnackbarOpen(true);
        setSeverity('success');
      },
      (error) => {
        setMessage(error.message);
        setSnackbarOpen(true);
        setSeverity('error');
      }
    );
  };
  const handleConfirm = async () => {
    setDialogOpen(false);
    await deleteImage();
    if (setRefresh) {
      setTimeout(async () => {
        setRefresh((refresh) => !refresh);
      }, 500);
    }
  };

  return (
    <Box sx={{ m: 1 }}>
      <Tooltip title={t('action.delete')}>
        <Button
          variant="outlined"
          color="error"
          onClick={handleSubmit}
          id={`download-${ids}`}
          aria-label="delete-photo"
        >
          <Delete />
        </Button>
      </Tooltip>
      <ConfirmationDialog
        open={dialogOpen}
        onConfirm={handleConfirm}
        onCancel={() => {
          setDialogOpen(false);
        }}
        title={t('action.confirmDelete')}
        message={t('photo.warningDeleting')}
      />
      <AlertSnackbar
        open={snackbarOpen}
        severity={severity}
        message={t(message)}
        onClose={setSnackbarOpen}
      />
    </Box>
  );
};
