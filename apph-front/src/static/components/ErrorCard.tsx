import { Card, CardContent, CardHeader, Typography } from '@mui/material';
import * as React from 'react';
import { useTranslation } from 'react-i18next';
import { cardStyle } from '../../utils';

export const ErrorCard = ({
  errorMessage
}: {
  errorMessage: string;
}): JSX.Element => {
  const { t } = useTranslation();
  return (
    <Card sx={cardStyle}>
      <CardHeader title={t('error.error')} />
      <CardContent>
        <Typography variant="body2">Message: {t(errorMessage)}</Typography>
      </CardContent>
    </Card>
  );
};
