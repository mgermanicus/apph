import { Navigate, Route, Routes } from 'react-router-dom';
import { UserProfilePage } from './static/pages/UserProfilePage';
import { SignIn } from './static/components/SignIn';
import { SignUp } from './static/components/SignUp';
import { TODOPage } from './static/pages/TODOPage';
import { MyFoldersPage } from './static/pages/MyFoldersPage';
import { PrivatePageContainer } from './static/pages/PrivatePageContainer';
import { useSelector } from 'react-redux';
import { IUser } from './utils';

export const PrivateRoutes = (): JSX.Element => {
  const user = useSelector(
    ({ currentUser }: { currentUser: IUser }) => currentUser
  );
  const authenticated = !!user.login;
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
        <Route path="/signUp" element={<SignUp />} />
        <Route
          path="/me"
          element={needAuthenticationRoute(
            <PrivatePageContainer element={<UserProfilePage />} />
          )}
        />
        <Route
          path="/pictures"
          element={needAuthenticationRoute(
            <PrivatePageContainer
              element={<TODOPage todo="Page: Mes Photos" />}
            />
          )}
        />
        <Route
          path="/folders"
          element={needAuthenticationRoute(
            <PrivatePageContainer element={<MyFoldersPage />} />
          )}
        />
        <Route
          path="/trips"
          element={needAuthenticationRoute(
            <PrivatePageContainer
              element={<TODOPage todo="Page: Mes Voyages" />}
            />
          )}
        />
        <Route
          path="/tags"
          element={needAuthenticationRoute(
            <PrivatePageContainer
              element={<TODOPage todo="Page: Mes Tags" />}
            />
          )}
        />
        <Route
          path="/treatments"
          element={needAuthenticationRoute(
            <PrivatePageContainer
              element={<TODOPage todo="Page: Mes Traitements" />}
            />
          )}
        />
        <Route
          path="/research"
          element={needAuthenticationRoute(
            <PrivatePageContainer
              element={<TODOPage todo="Page: Rechercher Avancée" />}
            />
          )}
        />
      </Routes>
    </>
  );
};
