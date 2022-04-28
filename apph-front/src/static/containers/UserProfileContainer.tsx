import { UserProfile } from '../components/UserProfile';
import { useEffect, useState } from 'react';
import { IUser } from '../../utils/types';
import { ErrorCard } from '../components/ErrorCard';
import UserService from '../../services/UserService';
import { Button, Stack } from '@mui/material';
import { EditProfile } from '../components/EditProfile';

export const UserProfileContainer = (): JSX.Element => {
  const [firstname, setFirstname] = useState<string>('');
  const [lastname, setLastname] = useState<string>('');
  const [email, setEmail] = useState<string>('');
  const [error, setError] = useState<string>();
  const [editMode, setEditMode] = useState<boolean>(false);

  const updateUser = () =>
    UserService.getUser(
      (user: string) => {
        const userConverted: IUser = JSON.parse(user);
        setFirstname(userConverted.firstname);
        setLastname(userConverted.lastname);
        setEmail(userConverted.login);
      },
      (errorMessage: string) => {
        setError(errorMessage);
      }
    );

  useEffect(() => {
    (async () => {
      await updateUser();
    })();
  }, [firstname, lastname, email]);

  const handleEdit = async () => {
    await updateUser();
    setEditMode(false);
  };

  return (
    <Stack
      direction="column"
      justifyContent="flex-start"
      alignItems="center"
      spacing={0}
    >
      {error ? (
        <ErrorCard errorMessage={error} />
      ) : editMode ? (
        <>
          <EditProfile
            firstname={firstname}
            lastname={lastname}
            login={email}
            onEdit={handleEdit}
          />
        </>
      ) : (
        <>
          <UserProfile
            firstname={firstname}
            lastname={lastname}
            login={email}
          />
          <Button onClick={() => setEditMode(true)}>Modifier</Button>
        </>
      )}
    </Stack>
  );
};
