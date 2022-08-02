import { Card, CardHeader, IconButton, Tooltip } from '@mui/material';
import * as React from 'react';
import { UserAvatar } from './UserAvatar';
import { Delete } from '@mui/icons-material';
import { useTranslation } from 'react-i18next';

export const ContactCard = ({
  firstname,
  lastname,
  login
}: {
  firstname: string;
  lastname: string;
  login: string;
}): JSX.Element => {
  const { t } = useTranslation();

  return (
    <Card>
      <CardHeader
        title={`${firstname} ${lastname}`}
        subheader={login}
        avatar={<UserAvatar firstname={firstname} lastname={lastname} />}
        action={
          <Tooltip title={t('action.delete')}>
            <IconButton>
              <Delete />
            </IconButton>
          </Tooltip>
        }
      />
    </Card>
  );
};
