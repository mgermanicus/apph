import { UserProfile } from '../components/UserProfile';

export const UserProfileContainer = (): JSX.Element => {
  return (
    <UserProfile
      firstname={'Min'}
      lastname={'SUN'}
      email={'min.sun@viseo.com'}
    />
  );
};
