import React, { useState } from 'react';
import { AlertColor, Button } from '@mui/material';
import { ConfirmationDialog } from './ConfirmationDialog';
import DeleteIcon from '@mui/icons-material/Delete';
import PhotoService from '../../services/PhotoService';
import { AlertSnackbar } from './AlertSnackbar';

export const DeleteImage = ({ ids }: { ids: number[] }): JSX.Element => {
  const [message, setMessage] = useState('');
  const [dialogOpen, setDialogOpen] = useState<boolean>(false);
  const [snackbarOpen, setSnackbarOpen] = useState<boolean>(false);
  const [severity, setSeverity] = useState<AlertColor>();

  const handleSubmit = () => {
    if (ids.length != 0) {
      setDialogOpen(true);
    } else {
      setSnackbarOpen(true);
      setMessage('Aucune photo sélectionnée');
      setSeverity('warning');
    }
  };
  const deleteImage = () => {
    PhotoService.deleteImage(
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
  const handleConfirm = () => {
    setDialogOpen(false);
    console.log(ids);
    deleteImage();
  };

  return (
    <>
      <Button
        variant="outlined"
        color="error"
        startIcon={<DeleteIcon />}
        onClick={handleSubmit}
        id={`download-${ids}`}
      >
        Supprimer
      </Button>
      <ConfirmationDialog
        open={dialogOpen}
        onConfirm={handleConfirm}
        onCancel={() => {
          setDialogOpen(false);
        }}
        title="Confirmez-vous la suppression?"
        message="Si vous confirmez, vos photos seront définitivement effacés"
      />
      <AlertSnackbar
        open={snackbarOpen}
        severity={severity}
        message={message}
        onClose={setSnackbarOpen}
      />
    </>
  );
};
