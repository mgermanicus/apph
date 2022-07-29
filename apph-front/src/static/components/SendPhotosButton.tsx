import React, { FormEvent, useState } from 'react';
import {
  AlertColor,
  Box,
  Button,
  Dialog,
  TextField,
  Tooltip
} from '@mui/material';
import { useTranslation } from 'react-i18next';
import AttachEmailIcon from '@mui/icons-material/AttachEmail';
import { AlertSnackbar } from './AlertSnackbar';
import Grid from '@mui/material/Grid';
import Typography from '@mui/material/Typography';
import { emailValidator } from '../../utils';
import PhotoService from '../../services/PhotoService';

export const SendPhotosButton = ({ ids }: { ids: number[] }) => {
  const [recipient, setRecipient] = useState<string>('');
  const [subject, setSubject] = useState<string>('');
  const [emailContent, setEmailContent] = useState<string>('');
  const [isFormOpen, setIsFormOpen] = useState<boolean>(false);
  const [errorMessage, setErrorMessage] = useState<string>('');
  const [snackBarSeverity, setSnackBarSeverity] =
    useState<AlertColor>('warning');
  const { t } = useTranslation();

  const openSnackBar = (severity: AlertColor, message: string) => {
    setSnackBarSeverity(severity);
    setErrorMessage(message);
  };

  const handleOpenForm = () => {
    if (ids.length != 0) {
      setIsFormOpen(true);
    } else {
      openSnackBar('warning', 'photo.noneSelected');
    }
  };

  const handleCloseForm = () => {
    setRecipient('');
    setSubject('');
    setEmailContent('');
    setIsFormOpen(false);
  };

  const handleSubmit = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    if (!emailValidator.test(recipient)) {
      setErrorMessage('signup.error.email');
    } else {
      await PhotoService.sendPhotos(
        recipient,
        subject,
        emailContent,
        ids,
        (success) => {
          openSnackBar('success', success);
          handleCloseForm();
        },
        (error) => openSnackBar('error', error)
      );
    }
  };

  return (
    <>
      <Box sx={{ m: 1 }}>
        <Tooltip title={t('photo.sendPhotos')}>
          <Button
            variant="outlined"
            onClick={handleOpenForm}
            aria-label="send-photos"
          >
            <AttachEmailIcon />
          </Button>
        </Tooltip>
      </Box>
      <Dialog open={isFormOpen} onClose={handleCloseForm}>
        <Typography
          component="h1"
          variant="h5"
          sx={{ textAlign: 'center', mt: '10px' }}
        >
          {t('photo.sendPhotos')}
        </Typography>
        <Box component="form" onSubmit={handleSubmit} sx={{ mt: 3, p: '20px' }}>
          <Grid container spacing={2}>
            <Grid item xs={12}>
              <TextField
                required
                fullWidth
                autoFocus
                id="email"
                label={t('email.to')}
                name="emailTo"
                autoComplete={t('email.to')}
                type="email"
                inputProps={{ maxLength: 255 }}
                onChange={(event) => setRecipient(event.target.value)}
              />
            </Grid>
            <Grid item xs={12}>
              <TextField
                required
                fullWidth
                id="subject"
                label={t('email.subject')}
                name="subject"
                autoComplete={t('subject')}
                inputProps={{ maxLength: 255 }}
                onChange={(event) => setSubject(event.target.value)}
              />
            </Grid>
            <Grid item xs={12}>
              <TextField
                required
                fullWidth
                id="content"
                label={t('email.content')}
                name="Content"
                autoComplete="Content"
                inputProps={{ maxLength: 255 }}
                onChange={(event) => setEmailContent(event.target.value)}
              />
            </Grid>
          </Grid>
          <Button
            type="submit"
            fullWidth
            variant="contained"
            sx={{ mt: 3, mb: 2 }}
          >
            {t('action.send')}
          </Button>
        </Box>
      </Dialog>
      <AlertSnackbar
        open={!!errorMessage}
        severity={snackBarSeverity}
        message={errorMessage}
        onClose={() => setErrorMessage('')}
      />
    </>
  );
};
