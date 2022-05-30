import React, { useState } from 'react';
import { AlertColor, Box, Tooltip } from '@mui/material';
import PhotoService from '../../services/PhotoService';
import { AlertSnackbar } from './AlertSnackbar';
import { IMessage, IPhoto } from '../../utils';
import { Download } from '@mui/icons-material';
import { LoadingButton } from '@mui/lab';

export const DownloadZip = ({ ids }: { ids: number[] }): JSX.Element => {
  const [message, setMessage] = useState('');
  const [snackbarOpen, setSnackbarOpen] = useState<boolean>(false);
  const [severity, setSeverity] = useState<AlertColor>();
  const [loading, setLoading] = useState<boolean>(false);

  const handleSubmit = () => {
    setLoading(true);
    if (ids.length != 0) {
      downloadImage();
    } else {
      setSnackbarOpen(true);
      setMessage('Aucune photo sélectionnée');
      setSeverity('warning');
      setLoading(false);
    }
  };
  const downloadImage = () => {
    PhotoService.downloadZip(
      ids,
      (photos: IPhoto) => {
        const imageBase64 = `data:application/zip;base64,${photos.data}`;
        const a = document.createElement('a');
        const event = new MouseEvent('click');
        a.href = imageBase64;
        a.download = photos.title + photos.format;
        a.dispatchEvent(event);
      },
      (error: IMessage) => {
        setMessage(error.message);
        setSnackbarOpen(true);
        setSeverity('error');
      }
    ).then(() => setLoading(false));
  };

  return (
    <Box sx={{ m: 1 }}>
      <Tooltip title="Télécharger photos">
        <LoadingButton
          variant="outlined"
          onClick={handleSubmit}
          id={`download-${ids}`}
          aria-label="downdload-zip"
          loading={loading}
        >
          <Download />
        </LoadingButton>
      </Tooltip>
      <AlertSnackbar
        open={snackbarOpen}
        severity={severity}
        message={message}
        onClose={setSnackbarOpen}
      />
    </Box>
  );
};
