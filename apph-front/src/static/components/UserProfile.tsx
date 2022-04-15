import * as React from 'react';
import { makeCardStyles } from '../../utils/theme';
import { Card, CardContent, CardHeader, Typography } from '@mui/material';
import { IUser } from '../../utils/types/User';

export const UserProfile = ({
  firstname,
  lastname,
  login
}: IUser): JSX.Element => {
  const classes = makeCardStyles();
  return (
    <Card className={classes.cardStyle}>
      <CardHeader title={`${firstname} ${lastname}`} />
      <CardContent>
        <Typography variant="body2">Email: {login}</Typography>
      </CardContent>
    </Card>
  );
};
