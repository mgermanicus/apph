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
import { makeCardStyles } from '../../utils/theme';
import { FormEvent, useEffect, useState } from 'react';
import UserService from '../../services/UserService';
import { IEditedUser, IUser } from '../../utils/types';
import AuthService from '../../services/AuthService';
import { useNavigate } from 'react-router-dom';
import { ConfirmationDialog } from './ConfirmationDialog';

export const EditProfile = () => {
  const navigate = useNavigate();
  const [user, setUser] = useState<IUser>({
    firstname: '',
    lastname: '',
    login: ''
  });
  const [firstname, setFirstname] = useState('');
  const [lastname, setLastname] = useState('');
  const [login, setLogin] = useState('');
  const [password, setPassword] = useState('');
  const [passwordConfirmation, setPasswordConfirmation] = useState('');
  const [alertMessage, setAlertMessage] = useState<string>();
  const [errorOccured, setErrorOccured] = useState(false);
  const [dialogOpen, setDialogOpen] = useState<boolean>(false);
  const classes = makeCardStyles();

  const updateUser = (newUser: IUser) => {
    setUser((user) => ({ ...newUser }));
    setFirstname(newUser.firstname);
    setLastname(newUser.lastname);
    setLogin(newUser.login);
  };

  useEffect(() => {
    (async () => {
      await UserService.getUser(
        (user: string) => {
          const userConverted: IUser = JSON.parse(user);
          updateUser(userConverted);
        },
        (errorMessage: string) => {
          setErrorOccured(true);
          setAlertMessage(errorMessage);
        }
      );
    })();
  }, []);

  const editedFields = (): IEditedUser => {
    return {
      ...(firstname != user.firstname && { firstname }),
      ...(lastname != user.lastname && { lastname }),
      ...(login != user.login && { login }),
      ...(password && { password })
    };
  };

  const editUser = async () =>
    UserService.editUser(
      editedFields(),
      (body: string) => {
        if (editedFields().login) {
          AuthService.logout();
          navigate(0);
        } else {
          const newUser = JSON.parse(body);
          AuthService.editUser(newUser);
          updateUser(newUser);
          setErrorOccured(false);
          setAlertMessage('Le profil a bien été modifié.');
        }
      },
      (errorMessage: string) => {
        setErrorOccured(true);
        setAlertMessage(errorMessage);
      }
    );

  const handleSubmit = (event: FormEvent) => {
    event.preventDefault();
    if (password != passwordConfirmation) return;
    if (editedFields().login) {
      setDialogOpen(true);
    } else {
      editUser();
    }
  };

  const resetChanges = () => {
    setFirstname(user.firstname);
    setLastname(user.lastname);
    setLogin(user.login);
  };

  const displayAlert = () => {
    return (
      <Alert severity={errorOccured ? 'error' : 'success'}>
        {alertMessage}
      </Alert>
    );
  };

  return (
    <Card className={classes.cardStyle}>
      <CardHeader title="Modifier le profil" />
      <CardContent>
        <Box component="form" onSubmit={handleSubmit}>
          <Stack spacing={2}>
            <TextField
              required
              label="Nom"
              value={lastname}
              onChange={(e) => setLastname(e.target.value)}
            />
            <TextField
              required
              label="Prénom"
              value={firstname}
              onChange={(e) => setFirstname(e.target.value)}
            />
            <TextField
              required
              label="Login"
              value={login}
              onChange={(e) => setLogin(e.target.value)}
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
            <Button color="error" onClick={(e) => resetChanges()}>
              Annuler les modifications
            </Button>
            {alertMessage ? displayAlert() : <></>}
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
