import * as React from 'react';
import { IUser, cardStyle } from '../../utils';
import {
  Button,
  Card,
  CardContent,
  CardHeader,
  Typography
} from '@mui/material';

import { useNavigate } from 'react-router-dom';
export const UserProfile = ({
  firstname,
  lastname,
  login
}: IUser): JSX.Element => {
  const navigate = useNavigate();
  return (
    <Card sx={cardStyle}>
      <CardHeader title={`${firstname} ${lastname}`} />
      <CardContent>
        <Typography variant="body2">Email: {login}</Typography>
        <Button onClick={() => navigate('/me/edit')}>Modifier</Button>
      </CardContent>
    </Card>
  );
};
