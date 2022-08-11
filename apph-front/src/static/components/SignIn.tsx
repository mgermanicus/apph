import * as React from 'react';
import { ChangeEvent, useState } from 'react';
import Avatar from '@mui/material/Avatar';
import Button from '@mui/material/Button';
import CssBaseline from '@mui/material/CssBaseline';
import TextField from '@mui/material/TextField';
import FormControlLabel from '@mui/material/FormControlLabel';
import Checkbox from '@mui/material/Checkbox';
import Link from '@mui/material/Link';
import Box from '@mui/material/Box';
import LockOutlinedIcon from '@mui/icons-material/LockOutlined';
import Typography from '@mui/material/Typography';
import Container from '@mui/material/Container';
import {
  Alert,
  AlertColor,
  Collapse,
  IconButton,
  Input,
  Modal,
  SxProps
} from '@mui/material';
import CloseIcon from '@mui/icons-material/Close';
import AuthService from '../../services/AuthService';
import { useDispatch } from 'react-redux';
import { changeCurrentUser } from '../../redux/slices/userSlice';
import { flagStyles, IUser } from '../../utils';
import { useNavigate } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import { setCookieLanguage } from '../../utils/setCookieLanguage';
import { AlertSnackbar } from './AlertSnackbar';

const Copyright = (props: { sx: SxProps }) => {
  return (
    <Typography
      variant="body2"
      color="text.secondary"
      align="center"
      {...props}
    >
      {'Copyright © '}
      <Link color="inherit" href="https://youtu.be/dQw4w9WgXcQ">
        Apph
      </Link>{' '}
      {new Date().getFullYear()}
      {'.'}
    </Typography>
  );
};
const style = {
  position: 'absolute' as const,
  top: '50%',
  left: '50%',
  transform: 'translate(-50%, -50%)',
  width: 400,
  bgcolor: 'background.paper',
  border: '2px solid #000',
  boxShadow: 24,
  p: 4
};
export const SignIn = () => {
  const [errorMessage, setErrorMessage] = useState<string>('');
  const [open, setOpen] = React.useState<boolean>(false);
  const handleOpen = () => setOpen(true);
  const handleClose = () => {
    setOpen(false);
    setResetEmail('');
  };
  const [resetEmail, setResetEmail] = useState<string>('');
  const [snackbarMessage, setSnackbarMessage] = useState<string>('');
  const [snackSeverity, setSnackSeverity] = useState<AlertColor>('error');
  const [snackbarOpen, setSnackbarOpen] = useState<boolean>(false);
  const dispatch = useDispatch();
  const navigate = useNavigate();
  const { t, i18n } = useTranslation();
  const emailValidator =
    /^(([^<>()[\]\\.,;:\s@"]+(\.[^<>()[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
  const handleSubmit = (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    const data = new FormData(event.currentTarget);
    const email = data.get('email')?.toString();
    const password = data.get('password')?.toString();
    if (!emailValidator.test(email ? email : '')) {
      setErrorMessage('signin.error.email');
    } else if (email && password) {
      AuthService.signIn(
        email,
        password,
        (user: IUser) => {
          dispatch(changeCurrentUser(user));
          navigate('/pictures');
        },
        (error: string) => {
          setErrorMessage(error);
        }
      );
    }
  };
  const handleReset = () => {
    if (emailValidator.test(resetEmail)) {
      AuthService.forgetPassword(
        resetEmail,
        i18n.language,
        () => {
          setSnackbarMessage(`${t('user.emailSend')} ${resetEmail}`);
          setSnackSeverity('success');
          setSnackbarOpen(true);
        },
        (errorMessage: string) => {
          setSnackbarMessage(errorMessage);
          setSnackSeverity('error');
          setSnackbarOpen(true);
        }
      );
    } else {
      setSnackbarMessage('user.error.email');
      setSnackSeverity('warning');
      setSnackbarOpen(true);
    }
  };
  const handleChange = (event: ChangeEvent<HTMLInputElement>) => {
    setResetEmail(event.target.value);
  };

  return (
    <Container component="main" maxWidth="xs">
      <CssBaseline />
      <Box
        sx={{
          marginTop: 8,
          display: 'flex',
          flexDirection: 'column',
          alignItems: 'center'
        }}
      >
        <Avatar sx={{ m: 1, bgcolor: 'secondary.main' }}>
          <LockOutlinedIcon />
        </Avatar>
        <Typography component="h1" variant="h5">
          {t('signin.login')}
        </Typography>
        <Box component="form" onSubmit={handleSubmit} sx={{ mt: 1 }}>
          <TextField
            margin="normal"
            required
            fullWidth
            id="email"
            label={t('user.email')}
            name="email"
            autoComplete="email"
            autoFocus
            type="email"
          />
          <TextField
            margin="normal"
            required
            fullWidth
            name="password"
            label={t('user.password')}
            type="password"
            id="password"
            autoComplete="current-password"
          />
          <FormControlLabel
            control={<Checkbox value="remember" color="primary" />}
            label={t('signin.rememberMe')}
          />
          <Button
            type="submit"
            fullWidth
            variant="contained"
            sx={{ mt: 3, mb: 2 }}
          >
            {t('signin.login')}
          </Button>
          <div>
            <Link href="#" variant="body2" onClick={handleOpen}>
              {t('signin.forgottenPassword')}
            </Link>
            <AlertSnackbar
              open={snackbarOpen}
              severity={snackSeverity}
              message={t(snackbarMessage)}
              onClose={setSnackbarOpen}
            />
            <Modal
              open={open}
              onClose={handleClose}
              aria-labelledby="modal-modal-title"
              aria-describedby="modal-modal-description"
            >
              <Box sx={style}>
                <Typography id="modal-modal-title" variant="h6" component="h2">
                  {t('user.forgottenPassword')}
                </Typography>
                <Typography id="modal-modal-description" sx={{ mt: 2 }}>
                  {t('user.setEmailToReset')}
                </Typography>
                <Input
                  type="text"
                  placeholder="Email"
                  data-testid="emailReset"
                  onChange={handleChange}
                />
                <Button onClick={handleReset}>{t('user.resetPassword')}</Button>
              </Box>
            </Modal>
          </div>
          <br />
          <Link href="signUp" variant="body2">
            {t('signin.noAccount')}
          </Link>
        </Box>
      </Box>
      <Copyright sx={{ mt: 8, mb: 4 }} />
      <button
        onClick={() => {
          setCookieLanguage('fr');
          i18n.changeLanguage('fr');
        }}
        style={flagStyles.fr}
      />
      <button
        onClick={() => {
          setCookieLanguage('en');
          i18n.changeLanguage('en');
        }}
        style={flagStyles.en}
      />
      <Collapse in={errorMessage !== ''}>
        <Alert
          action={
            <IconButton
              aria-label="close"
              color="inherit"
              size="small"
              onClick={() => {
                setErrorMessage('');
              }}
            >
              <CloseIcon fontSize="inherit" />
            </IconButton>
          }
          sx={{ mb: 2 }}
          severity="error"
        >
          {t(errorMessage)}
        </Alert>
      </Collapse>
    </Container>
  );
};
