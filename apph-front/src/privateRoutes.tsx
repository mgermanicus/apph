import { Navigate, Route, Routes } from 'react-router-dom';
import { UserProfilePage } from './static/pages/UserProfilePage';
import { SignIn } from './static/components/SignIn';
import { SignUp } from './static/components/SignUp';
import { TODOPage } from './static/pages/TODOPage';
import { MyFoldersPage } from './static/pages/MyFoldersPage';
import { PrivatePageContainer } from './static/pages/PrivatePageContainer';
import { useSelector } from 'react-redux';
import { IUser } from './utils';
import { EditProfile } from './static/components/EditProfile';
import { MyPhotoPage } from './static/pages/MyPhotoPage';
import { AdvancedResearchPage } from './static/pages/AdvancedResearchPage';
import { UserListPage } from './static/pages/UserListPage';

export const PrivateRoutes = ({
  authenticated
}: {
  authenticated: boolean;
}): JSX.Element => {
  const user = useSelector(
    ({ currentUser }: { currentUser: IUser }) => currentUser
  );
  const isAuthenticated = authenticated !== !!user.login || !!user.login;
  const needAuthenticationRoute = (element: JSX.Element): JSX.Element => {
    return isAuthenticated ? element : <Navigate to="/" />;
  };

  const needNoAuthenticationRoute = (element: JSX.Element): JSX.Element => {
    return !isAuthenticated ? element : <Navigate to="/pictures" />;
  };

  const needAdminAuthenticationRoute = (element: JSX.Element): JSX.Element => {
    return isAuthenticated && user.isAdmin ? element : <Navigate to={'/'} />;
  };

  return (
    <>
      <Routes>
        <Route path="/" element={needNoAuthenticationRoute(<SignIn />)} />
        <Route path="/signUp" element={needNoAuthenticationRoute(<SignUp />)} />
        <Route
          path="/me"
          element={needAuthenticationRoute(
            <PrivatePageContainer element={<UserProfilePage />} />
          )}
        />
        <Route
          path="/me/edit"
          element={needAuthenticationRoute(
            <PrivatePageContainer element={<EditProfile />} />
          )}
        />
        <Route
          path="/pictures"
          element={needAuthenticationRoute(
            <PrivatePageContainer element={<MyPhotoPage />} />
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
            <PrivatePageContainer element={<AdvancedResearchPage />} />
          )}
        />
        <Route
          path="/users"
          element={needAdminAuthenticationRoute(
            <PrivatePageContainer element={<UserListPage />} />
          )}
        />
        <Route path="*" element={<Navigate to="/" />} />
      </Routes>
    </>
  );
};
