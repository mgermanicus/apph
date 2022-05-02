import { makeCardStyles } from '../../utils';
import { Card, CardContent, CardHeader, Typography } from '@mui/material';
import * as React from 'react';

//TODO replace this component by the corresponding component page in privateRoutes.tsx
//TODO remove this file when all pages are done
export const TODOPage = ({ todo }: { todo: string }): JSX.Element => {
  const classes = makeCardStyles();
  return (
    <Card className={classes.cardStyle}>
      <CardHeader title="TODO" />
      <CardContent>
        <Typography>{todo}</Typography>
      </CardContent>
    </Card>
  );
};
