import { UserProfile } from '../components/UserProfile';
import { useEffect, useState } from 'react';
import { IUser } from '../../utils';
import { ErrorCard } from '../components/ErrorCard';
import UserService from '../../services/UserService';
import { useTranslation } from 'react-i18next';

export const UserProfilePage = (): JSX.Element => {
  const [firstname, setFirstname] = useState<string>('');
  const [lastname, setLastname] = useState<string>('');
  const [email, setEmail] = useState<string>('');
  const [error, setError] = useState<string>();
  const { t } = useTranslation();

  useEffect(() => {
    (async () => {
      await UserService.getUser(
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
    })();
  }, []);

  return (
    <>
      {error ? (
        <ErrorCard errorMessage={t(error)} />
      ) : (
        <UserProfile firstname={firstname} lastname={lastname} login={email} />
      )}
    </>
  );
};
