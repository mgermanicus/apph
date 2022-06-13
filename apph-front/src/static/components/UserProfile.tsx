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
import { useTranslation } from 'react-i18next';

export const UserProfile = ({
  firstname,
  lastname,
  login
}: IUser): JSX.Element => {
  const navigate = useNavigate();
  const { t } = useTranslation();
  return (
    <Card sx={cardStyle}>
      <CardHeader title={`${firstname} ${lastname}`} />
      <CardContent>
        <Typography variant="body2">
          {t('user.email')}: {login}
        </Typography>
        <Button onClick={() => navigate('/me/edit')}>
          {t('action.modify')}
        </Button>
      </CardContent>
    </Card>
  );
};
