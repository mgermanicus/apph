import { render } from '@testing-library/react';
import {
  clickButton,
  JWS_TOKEN,
  triggerRequestFailure,
  triggerRequestSuccess
} from '../utils';
import { screen } from '@testing-library/dom';
import { MovePhotoOrFolder } from '../../static/components/MovePhotoOrFolder';
import React from 'react';
import Cookies from 'universal-cookie';
import jwtDecode from 'jwt-decode';
import { FolderService } from '../../services/FolderService';

describe('Test MovePhoto', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('render move photo with success', () => {
    //GIVEN
    triggerRequestSuccess(
      '{"id":1,"version":0,"name":"Elie_root","parentFolderId":null,"childrenFolders":[]}'
    );
    const cookies = new Cookies();
    const decodedToken = jwtDecode(JWS_TOKEN);
    if (decodedToken !== null && typeof decodedToken === 'object') {
      cookies.set('user', { ...decodedToken, token: JWS_TOKEN });
    }
    render(<MovePhotoOrFolder photoIds={[1]} />);
    clickButton(/move-photo/i);
    //WHEN
    triggerRequestSuccess(
      '{"messageList":["warning: L\'une des photos est déjà dans le dossier.","error: L\'une des photos comporte un nom existant déjà dans le dossier destinataire.","success: Le déplacement des photos est terminé."]}'
    );
    clickButton(/folder.move/);
    //THEN
    expect(
      screen.getByText(/L'une des photos est déjà dans le dossier./)
    ).toBeInTheDocument();
    expect(
      screen.getByText(
        /L'une des photos comporte un nom existant déjà dans le dossier destinataire./
      )
    ).toBeInTheDocument();
    expect(
      screen.getByText(/Le déplacement des photos est terminé./)
    ).toBeInTheDocument();
  });

  it('render move photo with unauthorized access', () => {
    //GIVEN
    triggerRequestSuccess(
      '{"id":1,"version":0,"name":"Elie_root","parentFolderId":null,"childrenFolders":[]}'
    );
    const cookies = new Cookies();
    const decodedToken = jwtDecode(JWS_TOKEN);
    if (decodedToken !== null && typeof decodedToken === 'object') {
      cookies.set('user', { ...decodedToken, token: JWS_TOKEN });
    }
    render(<MovePhotoOrFolder photoIds={[1]} />);
    clickButton(/move-photo/i);
    //WHEN
    triggerRequestFailure(
      '{"message": "L\'utilisateur n\'a pas accès au dossier."}'
    );
    clickButton(/folder.move/);
    //THEN
  });

  it('render move photo without selected photos', () => {
    //GIVEN
    triggerRequestSuccess(
      '{"id":1,"version":0,"name":"Elie_root","parentFolderId":null,"childrenFolders":[]}'
    );
    const cookies = new Cookies();
    const decodedToken = jwtDecode(JWS_TOKEN);
    if (decodedToken !== null && typeof decodedToken === 'object') {
      cookies.set('user', { ...decodedToken, token: JWS_TOKEN });
    }
    render(<MovePhotoOrFolder photoIds={[]} />);
    //WHEN
    clickButton(/move-photo/i);
    //THEN
    expect(screen.getByText(/photo.noneSelected/)).toBeInTheDocument();
  });

  it('render move photo without parent folder', () => {
    //GIVEN
    jest.spyOn(FolderService, 'getFolders').mockResolvedValue();
    render(<MovePhotoOrFolder photoIds={[1]} />);
    //WHEN
    clickButton(/move-photo/i);
    //THEN
    expect(screen.getByText(/folder.error.parentNotExist/)).toBeInTheDocument();
  });
});
