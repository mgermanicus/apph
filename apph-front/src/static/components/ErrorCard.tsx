import { makeCardStyles } from '../../utils/theme';
import { Card, CardContent, CardHeader, Typography } from '@mui/material';
import * as React from 'react';

interface IError {
  statusCode: number;
  errorMessage: string;
}

export const ErrorCard = ({
  statusCode,
  errorMessage
}: IError): JSX.Element => {
  const classes = makeCardStyles();
  return (
    <Card className={classes.cardStyle}>
      <CardHeader title={`${statusCode} `} />
      <CardContent>
        <Typography variant="body2">Message: {errorMessage}</Typography>
      </CardContent>
    </Card>
  );
};
