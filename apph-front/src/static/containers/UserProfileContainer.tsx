import { UserProfile } from '../components/UserProfile';
import { useEffect, useState } from 'react';

export const UserProfileContainer = (): JSX.Element => {
  const [firstname, setFirstname] = useState<string>('');
  const [lastname, setLastname] = useState<string>('');
  const [email, setEmail] = useState<string>('');
  const [isLoading, setIsLoading] = useState<boolean>(true);
  const [error, setError] = useState<object | null>(null);
  useEffect(() => {
    fetch(`http://localhost:8080/user/123`, {
      method: 'GET'
    })
      .then((res) => {
        if (res.ok) {
          return res.json();
        }
      })
      .then((data) => {
        console.log(data);
        const { firstname, lastname, email } = data;
        setFirstname(firstname);
        setLastname(lastname);
        setEmail(email);
      })
      .catch((error) => setError(error))
      .finally(() => setIsLoading(false));
  }, []);

  return (
    <>
      {isLoading &&
        (error ? (
          <UserProfile firstname="BAD" lastname="BAD" email="Bad" />
        ) : (
          <UserProfile
            firstname={firstname}
            lastname={lastname}
            email={email}
          />
        ))}
    </>
  );
};
