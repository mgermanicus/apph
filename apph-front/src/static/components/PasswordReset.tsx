import { useNavigate, useSearchParams } from 'react-router-dom';
import * as React from 'react';
import { useEffect, useState } from 'react';
import AuthService from '../../services/AuthService';
import {
  AlertColor,
  Button,
  Input,
  InputLabel,
  Typography
} from '@mui/material';
import { useTranslation } from 'react-i18next';
import { AlertSnackbar } from './AlertSnackbar';

export const PasswordReset = () => {
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();
  const [loading, setLoading] = useState<boolean>(true);
  const [newPassword, setNewPassword] = useState<string>('');
  const [confirmPassword, setConfirmPassword] = useState<string>('');
  const [reset, setReset] = useState<boolean>(false);
  const [snackbarMessage, setSnackbarMessage] = useState<string>('');
  const [snackSeverity, setSnackSeverity] = useState<AlertColor>('error');
  const [snackbarOpen, setSnackbarOpen] = useState<boolean>(false);
  const { t } = useTranslation();
  const handleSuccessSendToken = () => {
    setReset(true);
  };
  const handleReset = () => {
    if (newPassword.length < 3) {
      setSnackbarMessage('user.error.tooShortPassword');
      setSnackSeverity('error');
      setSnackbarOpen(true);
    } else if (newPassword !== confirmPassword) {
      setSnackbarMessage('user.error.passwordNotMatch');
      setSnackSeverity('error');
      setSnackbarOpen(true);
    } else
      AuthService.resetPassword(
        token,
        newPassword,
        handleSuccessResetPassword,
        handleError
      );
  };
  const handleError = (error: string) => {
    setSnackbarMessage(error);
    setSnackSeverity('error');
    setSnackbarOpen(true);
    setTimeout(() => {
      navigate('/');
    }, 5000);
  };

  const handleSuccessResetPassword = () => {
    setSnackbarMessage(t('user.passwordChanged'));
    setSnackSeverity('success');
    setSnackbarOpen(true);
    setTimeout(() => {
      navigate('/');
    }, 5000);
  };
  const token = searchParams.get('token') || '';
  useEffect(() => {
    AuthService.sendToken(token, handleSuccessSendToken, handleError);
    setLoading(false);
  }, []);
  const form = (
    <>
      <Typography variant="h5" component="h1" sx={{ mb: 4, mt: 2 }}>
        {t('user.resetPassword')}
      </Typography>
      <InputLabel htmlFor="newPassword">{t('user.newPassword')}</InputLabel>
      <Input
        id="newPassword"
        data-testid="newPassword"
        value={newPassword}
        onChange={(e) => setNewPassword(e.currentTarget.value)}
      />
      <br />
      <br />
      <InputLabel htmlFor="confirmPassword" sx={{ mt: 2 }}>
        {t('user.confirmPassword')}
      </InputLabel>
      <Input
        id="confirmPassword"
        data-testid="confirmPassword"
        value={confirmPassword}
        onChange={(e) => setConfirmPassword(e.currentTarget.value)}
      />
      <br />
      <Button onClick={handleReset} sx={{ mt: 3 }}>
        {t('action.confirm')}
      </Button>
    </>
  );
  return (
    <>
      {loading && <h1>Chargement</h1>}
      {reset && form}
      <AlertSnackbar
        open={snackbarOpen}
        severity={snackSeverity}
        message={t(snackbarMessage)}
        onClose={setSnackbarOpen}
        position="top"
        sx={{ marginTop: 30 }}
      />
    </>
  );
};
