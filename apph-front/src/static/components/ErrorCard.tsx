import { Card, CardContent, CardHeader, Typography } from '@mui/material';
import * as React from 'react';
import { cardStyle } from '../../utils';

export const ErrorCard = ({
  errorMessage
}: {
  errorMessage: string;
}): JSX.Element => {
  return (
    <Card sx={cardStyle}>
      <CardHeader title={'ERROR'} />
      <CardContent>
        <Typography variant="body2">Message: {errorMessage}</Typography>
      </CardContent>
    </Card>
  );
};
