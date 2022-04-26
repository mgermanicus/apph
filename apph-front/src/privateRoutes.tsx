import { Route, Routes } from 'react-router-dom';
import { UserProfileContainer } from './static/containers/UserProfileContainer';
import { SignIn } from './static/components/SignIn';
import { TODOContainer } from './static/containers/TODOContainer';

export const PrivateRoutes = (): JSX.Element => {
  return (
    <Routes>
      <Route path="/login" element={<SignIn />} />
      <Route path="/me" element={<UserProfileContainer />} />
      <Route
        path="/pictures"
        element={<TODOContainer todo="Page: Mes Photos" />}
      />
      <Route
        path="/folders"
        element={<TODOContainer todo="Page: Mes Dossiers" />}
      />
      <Route
        path="/trips"
        element={<TODOContainer todo="Page: Mes Voyages" />}
      />
      <Route path="/tags" element={<TODOContainer todo="Page: Mes Tags" />} />
      <Route
        path="/treatments"
        element={<TODOContainer todo="Page: Mes Traitements" />}
      />
      <Route
        path="/research"
        element={<TODOContainer todo="Page: Recherche Avancée" />}
      />
    </Routes>
  );
};
