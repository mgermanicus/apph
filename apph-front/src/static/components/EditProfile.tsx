import {
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
import { useNavigate } from 'react-router-dom';

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
  const [error, setError] = useState<string>();
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
          setError(errorMessage);
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
  const handleSubmit = (event: FormEvent) => {
    event.preventDefault();
    UserService.editUser(
      editedFields(),
      (body: string) => {
        if (editedFields().login) {
          UserService.deleteCookies();
          navigate('/login');
        } else {
          const newUser = JSON.parse(body);
          UserService.updateCookies(newUser);
          updateUser(newUser);
        }
      },
      (errorMessage: string) => {
        setError(errorMessage);
      }
    );
  };

  return (
    <Card className={classes.cardStyle}>
      <CardHeader title="Modifier le profil" />
      <CardContent>
        <form onSubmit={handleSubmit}>
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
              label="Mot de passe"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
            />
            <TextField
              label="Confirmer le mot de passe"
              required={!!password}
              value={passwordConfirmation}
              onChange={(e) => setPasswordConfirmation(e.target.value)}
              error={password != passwordConfirmation}
              helperText={
                password != passwordConfirmation
                  ? 'La saisie ne correspond pas au mot de passe'
                  : ''
              }
            />
            <Button type="submit">Valider</Button>
          </Stack>
        </form>
      </CardContent>
    </Card>
  );
};
