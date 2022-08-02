import * as React from 'react';
import { render, screen } from '@testing-library/react';
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
import userEvent from '@testing-library/user-event';

describe('Create Folder Button Tests', () => {
  const onSetRootFolder = jest.fn();

  beforeEach(() => {
    const cookies = new Cookies();
    const decodedToken = jwtDecode(JWS_TOKEN);
    if (decodedToken !== null && typeof decodedToken === 'object') {
      cookies.set('user', { ...decodedToken, token: JWS_TOKEN });
    }
    render(
      <CreateFolderButton selected={'1'} setRootFolder={onSetRootFolder} />
    );
    jest.clearAllMocks();
  });

  const doAction = () => {
    clickButton(/folder.createFolder/);
    fillText(/folder.name/, 'folder.new');
    clickButton(/folder.create/);
  };

  it('open with success the creation dialog', () => {
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
    //WHEN
    doAction();
    //THEN
    expect(onSetRootFolder).toBeCalled();
  });

  it('create with error an folder', () => {
    //GIVEN
    triggerRequestFailure('{"message": "Error Message"}');
    //WHEN
    doAction();
    //THEN
    expect(screen.getByText('Error Message')).toBeInTheDocument();
    expect(onSetRootFolder).not.toBeCalled();
  });

  it('creates a folder with empty name', () => {
    //WHEN
    clickButton(/folder.createFolder/);
    fillText(/folder.name/, '');
    clickButton(/folder.create/);
    expect(screen.getByText(/folder.error.emptyFolder/)).toBeVisible();
    expect(onSetRootFolder).not.toBeCalled();
  });

  it('submit by pressing enter', async () => {
    //GIVEN
    triggerRequestSuccess(
      '{"id":1,"version":0,"name":"Elie_root","parentFolderId":null,"childrenFolders":[{"id":2,"version":0,"name":"Elie_child_1","parentFolderId":1,"childrenFolders":[]},{"id":3,"version":0,"name":"Elie_child_2","parentFolderId":1,"childrenFolders":[]}]}'
    );
    //WHEN
    clickButton(/folder.createFolder/);
    fillText(/folder.name/, 'folder.new');
    userEvent.keyboard('{Enter}');
    //THEN
    expect(onSetRootFolder).toBeCalled();
  });
});
