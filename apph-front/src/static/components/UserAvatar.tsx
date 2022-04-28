import { Avatar } from '@mui/material';
import * as React from 'react';
import { randomColorCodeFromString } from '../../utils';

export const UserAvatar = ({
  firstname,
  lastname
}: {
  firstname: string;
  lastname: string;
}): JSX.Element => {
  const avatar = (firstname: string, lastname: string) => {
    return {
      sx: {
        backgroundColor: randomColorCodeFromString(firstname + lastname)
      },
      children: `${firstname[0]}${lastname[0]}`
    };
  };
  return <Avatar {...avatar(firstname, lastname)} />;
};
