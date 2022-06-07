import React, { useState } from 'react';
import { AlertColor, Box, Tooltip } from '@mui/material';
import PhotoService from '../../services/PhotoService';
import { AlertSnackbar } from './AlertSnackbar';
import { IMessage, IPhoto } from '../../utils';
import CloudDownloadIcon from '@mui/icons-material/CloudDownload';
import { LoadingButton } from '@mui/lab';

export const DownloadZip = ({
  ids,
  titleZip = 'photos'
}: {
  ids: number[];
  titleZip?: string;
}): JSX.Element => {
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
      titleZip,
      (photos: IPhoto) => {
        const imageBase64 = `data:application/zip;base64,${photos.data}`;
        const link = document.createElement('a');
        const event = new MouseEvent('click');
        link.href = imageBase64;
        link.download = photos.title + photos.format;
        link.dispatchEvent(event);
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
          aria-label="download-zip"
          loading={loading}
        >
          <CloudDownloadIcon />
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