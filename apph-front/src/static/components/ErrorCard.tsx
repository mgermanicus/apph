import { makeCardStyles } from '../../utils';
import { Card, CardContent, CardHeader, Typography } from '@mui/material';
import * as React from 'react';

export const ErrorCard = ({
  errorMessage
}: {
  errorMessage: string;
}): JSX.Element => {
  const classes = makeCardStyles();
  return (
    <Card className={classes.cardStyle}>
      <CardHeader title={'ERROR'} />
      <CardContent>
        <Typography variant="body2">Message: {errorMessage}</Typography>
      </CardContent>
    </Card>
  );
};
