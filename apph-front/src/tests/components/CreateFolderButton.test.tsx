import * as React from 'react';
import { render } from '@testing-library/react';
import { screen } from '@testing-library/dom';
import { CreateFolderButton } from '../../static/components/CreateFolderButton';
import {
  clickButton,
  fillText,
  JWS_TOKEN,
  triggerRequestFailure,
  triggerRequestSuccess
} from '../utils';
import Cookies from 'universal-cookie';
import jwtDecode from 'jwt-decode';

jest.mock('react-i18next', () => ({
  // this mock makes sure any components using the translate hook can use it without a warning being shown
  useTranslation: () => {
    return {
      t: (str: string) => str
    };
  }
}));

describe('Create Folder Button Tests', () => {
  const onSetRootFolder = jest.fn();
  const doAction = () => {
    const cookies = new Cookies();
    const decodedToken = jwtDecode(JWS_TOKEN);
    if (decodedToken !== null && typeof decodedToken === 'object') {
      cookies.set('user', { ...decodedToken, token: JWS_TOKEN });
    }
    render(
      <CreateFolderButton selected={'1'} setRootFolder={onSetRootFolder} />
    );
    //WHEN
    clickButton(/folder.createFolder/);
    fillText(/folder.name/, 'folder.new');
    clickButton(/folder.create/);
  };
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('open with success the creation dialog', () => {
    //GIVEN
    render(
      <CreateFolderButton selected={'1'} setRootFolder={onSetRootFolder} />
    );
    //WHEN
    clickButton(/folder.createFolder/);
    //THEN
    expect(screen.getByText('folder.creation')).toBeInTheDocument();
  });

  it('create with success an folder', () => {
    //GIVEN
    triggerRequestSuccess(
      '{"id":1,"version":0,"name":"Elie_root","parentFolderId":null,"childrenFolders":[{"id":2,"version":0,"name":"Elie_child_1","parentFolderId":1,"childrenFolders":[]},{"id":3,"version":0,"name":"Elie_child_2","parentFolderId":1,"childrenFolders":[]}]}'
    );
    doAction();
    //THEN
    expect(onSetRootFolder).toBeCalled();
  });

  it('create with error an folder', () => {
    //GIVEN
    triggerRequestFailure('{"message": "Error Message"}');
    doAction();
    //THEN
    expect(screen.getByText('Error Message')).toBeInTheDocument();
    expect(onSetRootFolder).not.toBeCalled();
  });
});
