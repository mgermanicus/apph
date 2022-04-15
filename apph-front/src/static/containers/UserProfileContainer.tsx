import { UserProfile } from '../components/UserProfile';
import { useEffect, useState } from 'react';
import { UserService } from '../../services/UserService';
import { IUser } from '../../utils/types/User';
import { ErrorCard } from '../components/ErrorCard';

export const UserProfileContainer = (): JSX.Element => {
  const [firstname, setFirstname] = useState<string>('');
  const [lastname, setLastname] = useState<string>('');
  const [email, setEmail] = useState<string>('');
  const [error, setError] = useState<Response>();
  useEffect(() => {
    const fetchUserInfo = async () => {
      try {
        const response = await UserService.getUser();
        if (response?.ok) {
          const { firstname, lastname, login } =
            (await response.json()) as IUser;
          setFirstname(firstname);
          setLastname(lastname);
          setEmail(login);
        } else {
          setError(response);
        }
      } catch (e) {
        console.error('toto');
      }
    };
    fetchUserInfo().catch(console.error);
  }, []);

  return (
    <>
      {error ? (
        <ErrorCard statusCode={error.status} errorMessage={'User Not Found'} />
      ) : (
        <UserProfile firstname={firstname} lastname={lastname} login={email} />
      )}
    </>
  );
};
