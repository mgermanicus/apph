import { EditProfile } from '../../static/components/EditProfile';
import { render, waitFor } from '@testing-library/react';
import {
  clickButton,
  fakeRequest,
  fillPassword,
  fillText,
  spyCookies
} from '../utils';
import { screen } from '@testing-library/dom';
import { useNavigate } from 'react-router-dom';
import AuthService from '../../services/AuthService';
import Server from '../../services/Server';

const mockedUsedNavigate = jest.fn();
jest.mock('react-router-dom', () => ({
  ...jest.requireActual('react-router-dom'),
  useNavigate: () => mockedUsedNavigate
}));

describe('Test EditProfile', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('tests when a user edits firstname, lastname and password', () => {
    //GIVEN
    const user = {
      firstname: 'John',
      lastname: 'Doe',
      login: 'john.doe@email.com'
    };
    const editedUser = {
      firstname: 'Jean',
      lastname: 'Dupont',
      login: 'john.doe@email.com'
    };
    const spyUpdateUserCookie = spyCookies();
    const editedUserToken = 'edited user token';
    fakeRequest({
      '/user/': { body: JSON.stringify(user) },
      '/user/edit/': { body: editedUserToken }
    });
    render(<EditProfile />);
    //WHEN
    fillText(/Nom/, editedUser.lastname);
    fillText(/Prénom/, editedUser.firstname);
    fillPassword(/Mot de passe/, 'P@ssw0rd');
    fillPassword(/Confirmer le mot de passe/, 'P@ssw0rd');
    clickButton(/Valider/);
    //THEN
    expect(
      screen.getByText(/Le profil a bien été modifié/)
    ).toBeInTheDocument();
    expect(spyUpdateUserCookie).toBeCalledWith(editedUserToken);
  });

  it('tests the popup validation when user edits login', () => {
    //GIVEN
    const user = {
      firstname: 'John',
      lastname: 'Doe',
      login: 'john.doe@email.com'
    };
    const editedUser = {
      firstname: 'John',
      lastname: 'Doe',
      login: 'john.doe.edited@email.com'
    };
    fakeRequest({
      '/user/': { body: JSON.stringify(user) },
      '/user/edit': { body: 'edited user token' }
    });
    render(<EditProfile />);
    //WHEN
    fillText(/Login/, editedUser.login);
    clickButton(/Valider/);
    //THEN
    expect(screen.getByText(/Vous allez être déconnecté/)).toBeInTheDocument();
    expect(screen.getByText(/Continuer/)).toBeInTheDocument();
    expect(screen.getByText('Annuler')).toBeInTheDocument();
  });

  it('tests the popup cancel when user edits login', async () => {
    //GIVEN
    const user = {
      firstname: 'John',
      lastname: 'Doe',
      login: 'john.doe@email.com'
    };
    const editedUser = {
      firstname: 'John',
      lastname: 'Doe',
      login: 'john.doe.edited@email.com'
    };
    fakeRequest({
      '/user/': { body: JSON.stringify(user) }
    });
    render(<EditProfile />);
    //WHEN
    fillText(/Login/, editedUser.login);
    clickButton(/Valider/);
    clickButton(/^Annuler$/);
    //THEN
    await waitFor(() => {
      expect(
        screen.queryByText('Vous allez être déconnecté')
      ).not.toBeInTheDocument();
    });
  });

  it('tests when a user edits login', () => {
    //GIVEN
    const user = {
      firstname: 'John',
      lastname: 'Doe',
      login: 'john.doe@email.com'
    };
    const editedUser = {
      firstname: 'John',
      lastname: 'Doe',
      login: 'john.doe.edited@email.com'
    };
    AuthService.logout = jest.fn();
    fakeRequest({
      '/user/': { body: JSON.stringify(user) },
      '/user/edit/': { body: 'edited user token' }
    });
    render(<EditProfile />);
    //WHEN
    fillText(/Login/, editedUser.login);
    clickButton(/Valider/);
    clickButton(/Continuer/);
    //THEN
    expect(AuthService.logout).toBeCalled();
    expect(useNavigate()).toBeCalled();
  });

  it('tests when a user enters a wrong confirmation password', () => {
    //GIVEN
    const user = {
      firstname: 'John',
      lastname: 'Doe',
      login: 'john.doe@email.com'
    };
    fakeRequest({
      '/user/': { body: JSON.stringify(user) }
    });
    render(<EditProfile />);
    //WHEN
    fillPassword(/Mot de passe/, 'P@ssw0rd');
    fillPassword(/Confirmer le mot de passe/, 'WrongP@ssw0rd');
    clickButton(/Valider/);
    //THEN
    expect(
      screen.getByText(/Les mots de passe de correspondent pas/)
    ).toBeInTheDocument();
    expect(Server.request).not.toBeCalledWith(
      '/user/edit',
      expect.anything(),
      expect.anything(),
      expect.anything()
    );
  });

  it('tests when a user cancels changes', () => {
    //GIVEN
    const user = {
      firstname: 'John',
      lastname: 'Doe',
      login: 'john.doe@email.com'
    };
    const editedUser = {
      firstname: 'Jean',
      lastname: 'Dupont',
      login: 'john.doe@email.com'
    };
    fakeRequest({
      '/user/': { body: JSON.stringify(user) }
    });
    render(<EditProfile />);
    //WHEN
    fillText(/Nom/, editedUser.lastname);
    fillText(/Prénom/, editedUser.firstname);
    fillPassword(/Mot de passe/, 'P@ssw0rd');
    fillPassword(/Confirmer le mot de passe/, 'P@ssw0rd');
    clickButton(/Annuler les modifications/);
    //THEN
    expect(screen.getByDisplayValue(user.firstname)).toBeInTheDocument();
    expect(screen.getByDisplayValue(user.lastname)).toBeInTheDocument();
    expect(screen.getByDisplayValue(user.login)).toBeInTheDocument();
  });

  it('tests error handling when fetching user', () => {
    //GIVEN
    fakeRequest({
      '/user/': { error: 'Cannot get user data' }
    });
    render(<EditProfile />);
    //WHEN
    //THEN
    expect(screen.getByText(/Cannot get user data/)).toBeInTheDocument();
  });

  it('tests error handling when submitting form', () => {
    //GIVEN
    const user = {
      firstname: 'John',
      lastname: 'Doe',
      login: 'john.doe@email.com'
    };
    fakeRequest({
      '/user/': { body: JSON.stringify(user) },
      '/user/edit/': { error: 'Cannot edit user' }
    });
    render(<EditProfile />);
    //WHEN
    fillText(/Prénom/, 'Jean');
    clickButton(/Valider/);
    //THEN
    expect(screen.getByText(/Cannot edit user/)).toBeInTheDocument();
  });
});
