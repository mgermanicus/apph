import React, { useState } from 'react';
import { AlertColor, Box, Tooltip } from '@mui/material';
import PhotoService from '../../services/PhotoService';
import { AlertSnackbar } from './AlertSnackbar';
import { IFolder, IMessage, IPhoto } from '../../utils';
import CloudDownloadIcon from '@mui/icons-material/CloudDownload';
import { LoadingButton } from '@mui/lab';
import { useTranslation } from 'react-i18next';
import { FolderService } from '../../services/FolderService';

export const DownloadZip = ({
  ids,
  isFolder = false
}: {
  ids: number[];
  isFolder?: boolean;
}): JSX.Element => {
  const [message, setMessage] = useState('');
  const [snackbarOpen, setSnackbarOpen] = useState<boolean>(false);
  const [severity, setSeverity] = useState<AlertColor>();
  const [loading, setLoading] = useState<boolean>(false);
  const { t } = useTranslation();

  const linkDownloadZip = (title: string, data?: BinaryData) => {
    const imageBase64 = `data:application/zip;base64,${data}`;
    const link = document.createElement('a');
    const event = new MouseEvent('click');
    link.href = imageBase64;
    link.download = title + '.zip';
    link.dispatchEvent(event);
  };
  const traitError = (error: IMessage) => {
    setMessage(error.message);
    setSnackbarOpen(true);
    setSeverity('error');
  };
  const handleSubmit = () => {
    setLoading(true);
    if (ids.length != 0) {
      downloadImage();
    } else {
      setSnackbarOpen(true);
      setMessage('photo.noneSelected');
      setSeverity('warning');
      setLoading(false);
    }
  };
  const downloadImage = () => {
    if (isFolder) {
      FolderService.downloadFolder(
        ids[0],
        (folder: IFolder) => linkDownloadZip(folder.name, folder.data),
        (error) => traitError(error)
      ).then(() => setLoading(false));
    } else {
      PhotoService.downloadZip(
        ids,
        (photos: IPhoto) => linkDownloadZip(photos.title, photos.data),
        (error) => traitError(error)
      ).then(() => setLoading(false));
    }
  };

  return (
    <Box sx={{ m: 1 }}>
      <Tooltip title={isFolder ? t('folder.download') : t('photo.download')}>
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
        message={t(message)}
        onClose={setSnackbarOpen}
      />
    </Box>
  );
};
