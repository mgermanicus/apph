import * as React from 'react';
import { useState } from 'react';
import Avatar from '@mui/material/Avatar';
import Button from '@mui/material/Button';
import CssBaseline from '@mui/material/CssBaseline';
import TextField from '@mui/material/TextField';
import Link from '@mui/material/Link';
import Grid from '@mui/material/Grid';
import Box from '@mui/material/Box';
import LockOutlinedIcon from '@mui/icons-material/LockOutlined';
import Typography from '@mui/material/Typography';
import Container from '@mui/material/Container';
import { Alert, Collapse, IconButton, SxProps } from '@mui/material';
import CloseIcon from '@mui/icons-material/Close';
import { useNavigate } from 'react-router-dom';
import AuthService from '../../services/AuthService';
import { useTranslation } from 'react-i18next';
import { emailValidator, flagStyles, setCookieLanguage } from '../../utils';

function Copyright(props: { sx: SxProps }) {
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
}

export const SignUp = () => {
  const navigate = useNavigate();
  const [errorMessage, setErrorMessage] = useState('');
  const { t, i18n } = useTranslation();
  const handleSubmit = (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    const data = new FormData(event.currentTarget);
    const email = data.get('email')?.toString();
    const password = data.get('password')?.toString();
    const confirmPassword = data.get('confirmPassword')?.toString();
    const firstName = data.get('firstName')?.toString();
    const lastName = data.get('lastName')?.toString();
    if (!emailValidator.test(email ? email : '')) {
      setErrorMessage('signup.error.email');
    } else if (password && confirmPassword && password != confirmPassword) {
      setErrorMessage('signup.error.password');
    } else if (email && password && firstName && lastName && confirmPassword) {
      AuthService.signUp(
        email,
        password,
        firstName,
        lastName,
        () => {
          navigate('/');
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
          {t('signup.create')}
        </Typography>
        <Box component="form" onSubmit={handleSubmit} sx={{ mt: 3 }}>
          <Grid container spacing={2}>
            <Grid item xs={12} sm={6}>
              <TextField
                required
                fullWidth
                id="firstName"
                label={t('user.firstName')}
                name="firstName"
                autoComplete="given-name"
                autoFocus
                inputProps={{ maxLength: 127 }}
              />
            </Grid>
            <Grid item xs={12} sm={6}>
              <TextField
                required
                fullWidth
                id="lastName"
                label={t('user.lastName')}
                name="lastName"
                autoComplete="family-name"
                inputProps={{ maxLength: 127 }}
              />
            </Grid>
            <Grid item xs={12}>
              <TextField
                required
                fullWidth
                id="email"
                label={t('user.email')}
                name="email"
                autoComplete="email"
                type="email"
                inputProps={{ maxLength: 255 }}
              />
            </Grid>
            <Grid item xs={12}>
              <TextField
                required
                fullWidth
                id="password"
                label={t('user.password')}
                name="password"
                type="password"
                autoComplete="new-password"
              />
            </Grid>
            <Grid item xs={12}>
              <TextField
                required
                fullWidth
                id="confirmPassword"
                label={t('user.passwordConfirmation')}
                name="confirmPassword"
                type="password"
              />
            </Grid>
          </Grid>
          <Button
            type="submit"
            fullWidth
            variant="contained"
            sx={{ mt: 3, mb: 2 }}
          >
            {t('signup.create')}
          </Button>
          <Grid container justifyContent="center">
            <Grid item>
              <Link href="signIn" variant="body2">
                {t('signup.exist')}
              </Link>
            </Grid>
          </Grid>
        </Box>
      </Box>
      <Copyright sx={{ mt: 5, mb: 5 }} />
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
