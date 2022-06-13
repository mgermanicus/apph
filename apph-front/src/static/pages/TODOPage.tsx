import { cardStyle } from '../../utils';
import { Card, CardContent, CardHeader, Typography } from '@mui/material';
import * as React from 'react';

//TODO replace this component by the corresponding component page in privateRoutes.tsx
//TODO remove this file when all pages are done
export const TODOPage = ({ todo }: { todo: string }): JSX.Element => {
  return (
    <Card sx={cardStyle}>
      <CardHeader title="TODO" />
      <CardContent>
        <Typography>{todo}</Typography>
      </CardContent>
    </Card>
  );
};
