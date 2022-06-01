import {
  AlertColor,
  Box,
  Button,
  CircularProgress,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  Input,
  Tooltip
} from '@mui/material';
import { Upload } from '@mui/icons-material';
import React, { createRef, useState } from 'react';
import { AlertSnackbar } from './AlertSnackbar';
import PhotoService from '../../services/PhotoService';

export const ReUploadPhoto = ({
  photoId,
  updateData
}: {
  photoId: number;
  updateData: () => void;
}): JSX.Element => {
  const fileInput = createRef<HTMLInputElement>();
  const [showModal, setShowModal] = useState<boolean>(false);
  const [snackMessage, setSnackMessage] = useState<string>('');
  const [snackSeverity, setSnackSeverity] = useState<AlertColor>();
  const [snackbarOpen, setSnackbarOpen] = useState<boolean>(false);
  const [loading, setLoading] = useState<boolean>(false);

  const handleSubmit = () => {
    setLoading(true);
    const files = fileInput.current?.files;
    if (files) {
      const file = files[0];
      if (file) {
        PhotoService.reUploadImage(
          photoId,
          file,
          () => {
            updateData();
            setSnackSeverity('success');
            setSnackMessage("Le changement s'est effectué avec succèss.");
            setSnackbarOpen(true);
            setLoading(false);
            setShowModal(false);
          },
          (errorMessage) => {
            setSnackSeverity('error');
            setSnackMessage(errorMessage);
            setSnackbarOpen(true);
            setLoading(false);
          }
        );
      } else {
        setSnackSeverity('error');
        setSnackMessage('Veuillez sélectionner un fichier.');
        setSnackbarOpen(true);
        setLoading(false);
      }
    }
  };

  const handleOpenModal = () => {
    setShowModal(true);
    setSnackMessage('');
    setLoading(false);
  };

  const handleCloseModal = () => {
    if (!loading) {
      setShowModal(false);
    }
  };

  return (
    <Box>
      <Tooltip title="Changer d'image">
        <Button
          variant="outlined"
          onClick={handleOpenModal}
          aria-label="re-upload-photo"
        >
          <Upload />
        </Button>
      </Tooltip>
      <Dialog open={showModal} onClose={handleCloseModal}>
        <DialogTitle sx={{ fontWeight: 'bold' }}>Changer l'image</DialogTitle>
        <DialogContent>
          <Input
            fullWidth
            inputRef={fileInput}
            inputProps={{
              type: 'file',
              accept: 'image/*',
              'data-testid': 'file-input'
            }}
            required
          />
        </DialogContent>
        <DialogActions>
          <Button
            onClick={(event) => {
              event.preventDefault();
              handleSubmit();
            }}
            disabled={loading}
          >
            {loading ? <CircularProgress /> : <>Confirmer</>}
          </Button>
        </DialogActions>
      </Dialog>
      <AlertSnackbar
        open={snackbarOpen}
        severity={snackSeverity}
        message={snackMessage}
        onClose={setSnackbarOpen}
      />
    </Box>
  );
};
