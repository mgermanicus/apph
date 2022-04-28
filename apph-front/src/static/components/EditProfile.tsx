import {
  Button,
  Card,
  CardContent,
  CardHeader,
  Stack,
  TextField
} from '@mui/material';
import { makeCardStyles } from '../../utils/theme';
import { FormEvent, useState } from 'react';

type Props = {
  firstname: string;
  lastname: string;
  login: string;
  onEdit: () => void;
};

export const EditProfile = ({ onEdit, ...props }: Props) => {
  const [firstname, setFirstname] = useState(props.firstname);
  const [lastname, setLastname] = useState(props.lastname);
  const [login, setLogin] = useState(props.login);
  const [password, setPassword] = useState('');
  const [passwordConfirmation, setPasswordConfirmation] = useState('');
  const classes = makeCardStyles();

  const handleSubmit = (event: FormEvent) => {
    event.preventDefault();
    onEdit();
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
              value={passwordConfirmation}
              onChange={(e) => setPasswordConfirmation(e.target.value)}
            />
            <Button type="submit">Valider</Button>
          </Stack>
        </form>
      </CardContent>
    </Card>
  );
};
