import {
  Alert,
  Box,
  Button,
  Card,
  CardContent,
  CardHeader,
  Stack,
  TextField
} from '@mui/material';
import { cardStyle, IUser, IUserRequest } from '../../utils';
import { FormEvent, useEffect, useState } from 'react';
import UserService from '../../services/UserService';
import AuthService from '../../services/AuthService';
import { useNavigate } from 'react-router-dom';
import { ConfirmationDialog } from './ConfirmationDialog';
import { changeCurrentUser } from '../../redux/slices/userSlice';
import { useDispatch } from 'react-redux';

export const EditProfile = () => {
  const navigate = useNavigate();
  const [user, setUser] = useState<IUser>({
    firstname: '',
    lastname: '',
    login: ''
  });
  const [firstName, setFirstName] = useState('');
  const [lastName, setLastName] = useState('');
  const [login, setLogin] = useState('');
  const [password, setPassword] = useState('');
  const [passwordConfirmation, setPasswordConfirmation] = useState('');
  const [alertMessage, setAlertMessage] = useState<string>();
  const [errorOccured, setErrorOccured] = useState(false);
  const [dialogOpen, setDialogOpen] = useState<boolean>(false);

  const dispatch = useDispatch();

  useEffect(() => {
    (async () => {
      await UserService.getUser(
        (user: string) => {
          const userConverted: IUser = JSON.parse(user);
          setUser(userConverted);
          setFirstName(userConverted.firstname);
          setLastName(userConverted.lastname);
          setLogin(userConverted.login);
        },
        (errorMessage: string) => {
          setErrorOccured(true);
          setAlertMessage(errorMessage);
        }
      );
    })();
  }, []);

  const editedFields = (): IUserRequest => {
    return {
      ...(firstName != user.firstname && { firstName }),
      ...(lastName != user.lastname && { lastName }),
      ...(login != user.login && { email: login }),
      ...(password && { password })
    };
  };

  const displayAlert = (message: string) => {
    setAlertMessage(message);
    document.getElementById('alert')?.scrollIntoView();
  };

  const editUser = async () =>
    UserService.editUser(
      editedFields(),
      (newToken: string) => {
        if (editedFields().email) {
          AuthService.logout();
          navigate(0);
        } else {
          AuthService.updateUserCookie(newToken);
          dispatch(
            changeCurrentUser({
              firstname: firstName,
              login,
              lastname: lastName
            })
          );
          setErrorOccured(false);
          navigate('/me');
        }
      },
      (errorMessage: string) => {
        setErrorOccured(true);
        displayAlert(errorMessage);
      }
    );

  const handleSubmit = (event: FormEvent) => {
    event.preventDefault();
    if (password != passwordConfirmation) return;
    if (editedFields().email) {
      setDialogOpen(true);
    } else {
      editUser();
    }
  };

  const resetChanges = () => {
    setFirstName(user.firstname);
    setLastName(user.lastname);
    setLogin(user.login);
  };

  return (
    <Card sx={cardStyle}>
      <CardHeader title="Modifier le profil" />
      <CardContent>
        <Box component="form" onSubmit={handleSubmit}>
          <Stack spacing={2}>
            <TextField
              required
              label="Nom"
              value={lastName}
              inputProps={{ maxLength: 127 }}
              onChange={(e) => setLastName(e.target.value)}
            />
            <TextField
              required
              label="Prénom"
              value={firstName}
              inputProps={{ maxLength: 127 }}
              onChange={(e) => setFirstName(e.target.value)}
            />
            <TextField
              required
              label="Login"
              value={login}
              inputProps={{ maxLength: 255 }}
              onChange={(e) => setLogin(e.target.value)}
              type="email"
            />
            <TextField
              type="password"
              label="Mot de passe"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
            />
            <TextField
              type="password"
              label="Confirmer le mot de passe"
              required={!!password}
              value={passwordConfirmation}
              onChange={(e) => setPasswordConfirmation(e.target.value)}
              error={password != passwordConfirmation}
              helperText={
                password != passwordConfirmation
                  ? 'Les mots de passe de correspondent pas'
                  : ''
              }
            />
            <Button type="submit">Valider</Button>
            <Button color="error" onClick={resetChanges}>
              Annuler les modifications
            </Button>
            {alertMessage ? (
              <Alert severity={errorOccured ? 'error' : 'success'} id="alert">
                {alertMessage}
              </Alert>
            ) : (
              <></>
            )}
          </Stack>
        </Box>
        <ConfirmationDialog
          open={dialogOpen}
          onConfirm={() => {
            setDialogOpen(false);
            editUser();
          }}
          onCancel={() => {
            setDialogOpen(false);
          }}
          message="Vous allez être déconnecté"
        />
      </CardContent>
    </Card>
  );
};
