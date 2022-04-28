import { Navigate, Route, Routes } from 'react-router-dom';
import { UserProfilePage } from './static/pages/UserProfilePage';
import { SignIn } from './static/components/SignIn';
import { TODOPage } from './static/pages/TODOPage';
import { MyFoldersPage } from './static/pages/MyFoldersPage';
import AuthService from './services/AuthService';
import { Header } from './static/components/Header';

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
      <Header isAuth={authenticated} />
      <Routes>
        <Route path="*" element={needNoAuthenticationRoute(<SignIn />)} />
        <Route path="/" element={needNoAuthenticationRoute(<SignIn />)} />
        <Route
          path="/me"
          element={needAuthenticationRoute(<UserProfilePage />)}
        />
        <Route
          path="/pictures"
          element={needAuthenticationRoute(
            <TODOPage todo="Page: Mes Photos" />
          )}
        />
        <Route
          path="/folders"
          element={needAuthenticationRoute(<MyFoldersPage />)}
        />
        <Route
          path="/trips"
          element={needAuthenticationRoute(
            <TODOPage todo="Page: Mes Voyages" />
          )}
        />
        <Route
          path="/tags"
          element={needAuthenticationRoute(<TODOPage todo="Page: Mes Tags" />)}
        />
        <Route
          path="/treatments"
          element={needAuthenticationRoute(
            <TODOPage todo="Page: Mes Traitements" />
          )}
        />
        <Route
          path="/research"
          element={needAuthenticationRoute(
            <TODOPage todo="Page: Rechercher Avancée" />
          )}
        />
      </Routes>
    </>
  );
};
