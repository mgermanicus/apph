import { Navigate, Route, Routes } from 'react-router-dom';
import { UserProfilePage } from './static/pages/UserProfilePage';
import { SignIn } from './static/components/SignIn';
import { TODOPage } from './static/pages/TODOPage';
import { MyFoldersPage } from './static/pages/MyFoldersPage';
import AuthService from './services/AuthService';
import { RoutePage } from './static/pages/RoutePage';

export const PrivateRoutes = (): JSX.Element => {
  const authenticated = !!AuthService.getCurrentUser();
  const needAuthenticationRoute = (element: JSX.Element): JSX.Element => {
    return authenticated ? element : <Navigate to="/" />;
  };

  const needNoAuthenticationRoute = (element: JSX.Element): JSX.Element => {
    return !authenticated ? element : <Navigate to="/pictures" />;
  };
  return (
    <>
      <Routes>
        <Route path="*" element={needNoAuthenticationRoute(<SignIn />)} />
        <Route path="/" element={needNoAuthenticationRoute(<SignIn />)} />
        <Route
          path="/me"
          element={needAuthenticationRoute(
            <RoutePage element={<UserProfilePage />} />
          )}
        />
        <Route
          path="/pictures"
          element={needAuthenticationRoute(
            <RoutePage element={<TODOPage todo="Page: Mes Photos" />} />
          )}
        />
        <Route
          path="/folders"
          element={needAuthenticationRoute(
            <RoutePage element={<MyFoldersPage />} />
          )}
        />
        <Route
          path="/trips"
          element={needAuthenticationRoute(
            <RoutePage element={<TODOPage todo="Page: Mes Voyages" />} />
          )}
        />
        <Route
          path="/tags"
          element={needAuthenticationRoute(
            <RoutePage element={<TODOPage todo="Page: Mes Tags" />} />
          )}
        />
        <Route
          path="/treatments"
          element={needAuthenticationRoute(
            <RoutePage element={<TODOPage todo="Page: Mes Traitements" />} />
          )}
        />
        <Route
          path="/research"
          element={needAuthenticationRoute(
            <RoutePage element={<TODOPage todo="Page: Rechercher Avancée" />} />
          )}
        />
      </Routes>
    </>
  );
};
