import {
  Button,
  Card,
  CardContent,
  CardHeader,
  Typography
} from '@mui/material';
import { makeCardStyles } from '../../utils/theme';
import { IUser } from '../../utils/types';

type Props = {
  firstname: string;
  lastname: string;
  login: string;
  onEdit: () => void;
};

export const EditProfile = ({ firstname, lastname, login, onEdit }: Props) => {
  const classes = makeCardStyles();

  return (
    <Card className={classes.cardStyle}>
      <CardHeader title="Modifier le profil" />
      <CardContent>
        <Typography>Prénom: {firstname}</Typography>
        <Typography>Nom: {lastname}</Typography>
        <Typography>Login : {login}</Typography>
        <Button>Valider</Button>
      </CardContent>
    </Card>
  );
};
