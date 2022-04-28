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

let connected = false;

export const isConnected = () => {
  return connected;
};

export const resetConnected = () => {
  connected = false;
};

export const SignIn = () => {
  const [errorMessage, setErrorMessage] = useState('');
  const handleSubmit = (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    const data = new FormData(event.currentTarget);
    const email = data.get('email')?.toString();
    const password = data.get('password')?.toString();
    if (email && password) {
      AuthService.signIn(
        email,
        password,
        () => {
          connected = true;
          location.reload();
        },
        (errorMessage: string) => {
          setErrorMessage(errorMessage);
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
          Connexion
        </Typography>
        <Box component="form" onSubmit={handleSubmit} sx={{ mt: 1 }}>
          <TextField
            margin="normal"
            required
            fullWidth
            id="email"
            label="Adresse email"
            name="email"
            autoComplete="email"
            autoFocus
          />
          <TextField
            margin="normal"
            required
            fullWidth
            name="password"
            label="Mot de passe"
            type="password"
            id="password"
            autoComplete="current-password"
          />
          <FormControlLabel
            control={<Checkbox value="remember" color="primary" />}
            label="Se souvenir de moi"
          />
          <Button
            type="submit"
            fullWidth
            variant="contained"
            sx={{ mt: 3, mb: 2 }}
          >
            Connexion
          </Button>
          <Link href="#" variant="body2">
            Mot de passe oublié ?
          </Link>
          <br />
          <br />
          <Link href="#" variant="body2">
            Vous n'avez pas encore de compte ? Enregistrez-vous !
          </Link>
        </Box>
      </Box>
      <Copyright sx={{ mt: 8, mb: 4 }} />
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
          {errorMessage}
        </Alert>
      </Collapse>
    </Container>
  );
};
