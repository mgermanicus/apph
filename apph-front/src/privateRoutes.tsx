import { Route, Routes } from 'react-router-dom';
import { UserProfileContainer } from './static/containers/UserProfileContainer';
import { SignIn } from './static/components/SignIn';

export const PrivateRoutes = (): JSX.Element => {
  return (
    <Routes>
      <Route path="/login" element={<SignIn />} />
      <Route path="/me" element={<UserProfileContainer />} />
    </Routes>
  );
};
