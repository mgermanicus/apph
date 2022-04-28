import { Route, Routes } from 'react-router-dom';
import { UserProfilePage } from './static/pages/UserProfilePage';
import { SignIn } from './static/components/SignIn';
import { TODOPage } from './static/pages/TODOPage';
import { MyFoldersPage } from './static/pages/MyFoldersPage';

export const PrivateRoutes = (): JSX.Element => {
  return (
    <Routes>
      <Route path="/login" element={<SignIn />} />
      <Route path="/me" element={<UserProfilePage />} />
      <Route path="/pictures" element={<TODOPage todo="Page: Mes Photos" />} />
      <Route path="/folders" element={<MyFoldersPage />} />
      <Route path="/trips" element={<TODOPage todo="Page: Mes Voyages" />} />
      <Route path="/tags" element={<TODOPage todo="Page: Mes Tags" />} />
      <Route
        path="/treatments"
        element={<TODOPage todo="Page: Mes Traitements" />}
      />
      <Route
        path="/research"
        element={<TODOPage todo="Page: Rechercher Avancée" />}
      />
    </Routes>
  );
};
