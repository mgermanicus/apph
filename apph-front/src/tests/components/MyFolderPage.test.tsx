import * as React from 'react';
import {
  JWS_TOKEN,
  triggerRequestFailure,
  triggerRequestSuccess
} from '../utils';
import { render, screen } from '@testing-library/react';
import { MyFoldersPage } from '../../static/pages/MyFoldersPage';
import Cookies from 'universal-cookie';
import jwtDecode from 'jwt-decode';
import PhotoService from '../../services/PhotoService';

describe('MyFolderPage Tests', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('render folder tree with success', async () => {
    //GIVEN
    triggerRequestSuccess(
      '{"id":1,"version":0,"name":"Elie_root","parentFolderId":null,"childrenFolders":[]}'
    );
    jest.spyOn(PhotoService, 'getFolderPhotos').mockResolvedValue();
    const cookies = new Cookies();
    const decodedToken = jwtDecode(JWS_TOKEN);
    if (decodedToken !== null && typeof decodedToken === 'object') {
      cookies.set('user', { ...decodedToken, token: JWS_TOKEN });
    }
    //WHEN
    render(<MyFoldersPage />);

    expect(await screen.findByText(/Elie_root/)).toBeInTheDocument();
  });

  it('render folder tree with error', async () => {
    //GIVEN
    triggerRequestFailure('{"message": "User not found."}');
    const cookies = new Cookies();
    const decodedToken = jwtDecode(JWS_TOKEN);
    if (decodedToken !== null && typeof decodedToken === 'object') {
      cookies.set('user', { ...decodedToken, token: JWS_TOKEN });
    }
    //WHEN
    render(<MyFoldersPage />);
    //THEN
    expect(await screen.findByText(/User not found./)).toBeInTheDocument();
  });
});
