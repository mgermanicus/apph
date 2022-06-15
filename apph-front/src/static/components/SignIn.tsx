import * as React from 'react';
import { useState } from 'react';
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
import { Alert, Collapse, IconButton, SxProps } from '@mui/material';
import CloseIcon from '@mui/icons-material/Close';
import AuthService from '../../services/AuthService';
import { useDispatch } from 'react-redux';
import { changeCurrentUser } from '../../redux/slices/userSlice';
import { IUser, flagStyles } from '../../utils';
import { useNavigate } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import { setCookieLanguage } from '../../utils/setCookieLanguage';

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

export const SignIn = () => {
  const [errorMessage, setErrorMessage] = useState('');
  const dispatch = useDispatch();
  const navigate = useNavigate();
  const { t, i18n } = useTranslation();
  const handleSubmit = (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    const data = new FormData(event.currentTarget);
    const email = data.get('email')?.toString();
    const password = data.get('password')?.toString();
    const emailValidator =
      /^(([^<>()[\]\\.,;:\s@"]+(\.[^<>()[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
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
          <Link href="#" variant="body2">
            {t('signin.forgottenPassword')}
          </Link>
          <br />
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
