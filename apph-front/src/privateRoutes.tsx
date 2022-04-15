import { Route, Routes } from 'react-router-dom';
import { HeaderContainer } from './static/containers/HeaderContainer';
import { UserProfileContainer } from './static/containers/UserProfileContainer';

export const PrivateRoutes = (): JSX.Element => {
  return (
    <>
      <HeaderContainer />
      <Routes>
        <Route path="/me" element={<UserProfileContainer />} />
      </Routes>
    </>
  );
};
