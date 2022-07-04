import { render } from '@testing-library/react';

import {
  clickButton,
  JWS_TOKEN,
  triggerRequestFailure,
  triggerRequestSuccess
} from '../utils';
import { screen } from '@testing-library/dom';
import Cookies from 'universal-cookie';
import jwtDecode from 'jwt-decode';
import { DeleteFolder } from '../../static/components/DeleteFolder';

jest.mock('react-i18next', () => ({
  // this mock makes sure any components using the translate hook can use it without a warning being shown
  useTranslation: () => {
    return {
      t: (str: string) => str
    };
  }
}));

describe('Delete Folder tests', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('Render delete folder button and delete action', () => {
    //GIVEN
    const cookies = new Cookies();
    const decodedToken = jwtDecode(JWS_TOKEN);
    if (decodedToken !== null && typeof decodedToken === 'object') {
      cookies.set('user', { ...decodedToken, token: JWS_TOKEN });
    }
    triggerRequestSuccess('{ "message": "Success" }');
    render(
      <DeleteFolder
        selectedFolderId={'0'}
        refreshFolder={() => {
          return;
        }}
      />
    );
    //WHEN
    clickButton(/delete-folder/i);
    //THEN
    expect(
      screen.getByText('folder.deleteFolder.confirmDelete')
    ).toBeInTheDocument();
    //WHEN
    clickButton(/action.continue/);
    //THEN
    expect(
      screen.getByText('folder.deleteFolder.confirmMoveContent')
    ).toBeInTheDocument();
    //WHEN
    clickButton(/action.delete/);
    //THEN
    expect(screen.getByText('Success')).toBeInTheDocument();
  });

  it('Move content before delete', () => {
    //GIVEN
    const cookies = new Cookies();
    const decodedToken = jwtDecode(JWS_TOKEN);
    if (decodedToken !== null && typeof decodedToken === 'object') {
      cookies.set('user', { ...decodedToken, token: JWS_TOKEN });
    }
    triggerRequestSuccess(
      '{"id":1,"version":0,"name":"Elie_root","parentFolderId":null,"childrenFolders":[]}'
    );
    render(
      <DeleteFolder
        selectedFolderId={'0'}
        refreshFolder={() => {
          return;
        }}
      />
    );
    clickButton(/delete-folder/i);
    clickButton(/action.continue/);
    clickButton(/action.move/);
    //WHEN
    triggerRequestSuccess('{ "message": "Success" }');
    clickButton(/action.confirm/);
    //THEN
    expect(screen.getByText('Success')).toBeInTheDocument();
  });

  it('Delete with rootFolder error when moving', () => {
    //GIVEN
    const cookies = new Cookies();
    const decodedToken = jwtDecode(JWS_TOKEN);
    if (decodedToken !== null && typeof decodedToken === 'object') {
      cookies.set('user', { ...decodedToken, token: JWS_TOKEN });
    }
    triggerRequestFailure('{ "message": "Error" }');
    //WHEN
    render(
      <DeleteFolder
        selectedFolderId={'0'}
        refreshFolder={() => {
          return;
        }}
      />
    );
    //THEN
    expect(screen.getByText('Error')).toBeInTheDocument();
    //WHEN
    clickButton(/delete-folder/i);
    clickButton(/action.continue/);
    clickButton(/action.move/);
    //THEN
    expect(screen.getByText('folder.error.parentNotExist')).toBeInTheDocument();
  });

  it('Delete and Move a rootFolder', () => {
    //GIVEN
    const cookies = new Cookies();
    const decodedToken = jwtDecode(JWS_TOKEN);
    if (decodedToken !== null && typeof decodedToken === 'object') {
      cookies.set('user', { ...decodedToken, token: JWS_TOKEN });
    }
    triggerRequestSuccess(
      '{"id":1,"version":0,"name":"Elie_root","parentFolderId":null,"childrenFolders":[]}'
    );
    render(
      <DeleteFolder
        selectedFolderId={'1'}
        refreshFolder={() => {
          return;
        }}
      />
    );
    clickButton(/delete-folder/i);
    clickButton(/action.continue/);
    //WHEN
    clickButton(/action.move/);
    //THEN
    expect(screen.getByText('folder.error.moveFolder')).toBeInTheDocument();
  });

  it('Error when delete folder', () => {
    //GIVEN
    const cookies = new Cookies();
    const decodedToken = jwtDecode(JWS_TOKEN);
    if (decodedToken !== null && typeof decodedToken === 'object') {
      cookies.set('user', { ...decodedToken, token: JWS_TOKEN });
    }
    triggerRequestFailure('{ "message": "Error" }');
    render(
      <DeleteFolder
        selectedFolderId={'0'}
        refreshFolder={() => {
          return;
        }}
      />
    );
    clickButton(/delete-folder/i);
    clickButton(/action.continue/);
    //WHEN
    clickButton(/action.delete/);
    //THEN
    expect(screen.getByText('Error')).toBeInTheDocument();
  });
});
