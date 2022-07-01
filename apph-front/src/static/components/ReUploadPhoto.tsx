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
import React, { createRef, useEffect, useState } from 'react';
import { AlertSnackbar } from './AlertSnackbar';
import PhotoService from '../../services/PhotoService';
import { useTranslation } from 'react-i18next';
import SettingService from '../../services/SettingService';
import { ISetting } from '../../utils';

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
  const [uploadSize, setUploadSize] = useState<number>(10);
  const { t } = useTranslation();

  useEffect(() => {
    (async () => {
      await SettingService.getSettings(
        (data) => {
          const settings: ISetting = JSON.parse(data);
          setUploadSize(settings.uploadSize);
        },
        () => {
          return;
        }
      );
    })();
  }, []);

  const handleSubmit = () => {
    setLoading(true);
    const files = fileInput.current?.files;
    if (files) {
      const file = files[0];
      if (file) {
        PhotoService.reUploadImage(
          photoId,
          file,
          uploadSize,
          () => {
            updateData();
            setSnackSeverity('success');
            setSnackMessage('upload.successChange');
            setSnackbarOpen(true);
            setLoading(false);
            setTimeout(handleCloseModal, 1000);
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
        setSnackMessage('upload.maySelected');
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
      setSnackbarOpen(false);
    }
  };

  return (
    <Box>
      <Tooltip title={t('photo.change')}>
        <Button
          variant="outlined"
          onClick={handleOpenModal}
          aria-label="re-upload-photo"
        >
          <Upload />
        </Button>
      </Tooltip>
      <Dialog open={showModal} onClose={handleCloseModal}>
        <DialogTitle sx={{ fontWeight: 'bold' }}>
          {t('photo.change')}
        </DialogTitle>
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
            {loading ? <CircularProgress /> : <>{t('action.confirm')}</>}
          </Button>
        </DialogActions>
        <AlertSnackbar
          open={snackbarOpen}
          severity={snackSeverity}
          message={t(snackMessage)}
          onClose={setSnackbarOpen}
        />
      </Dialog>
    </Box>
  );
};
