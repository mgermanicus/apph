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

  it('render folder tree with success', () => {
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

    setTimeout(() => {
      expect(screen.getByText(/Elie_root/)).toBeInTheDocument();
    }, 500);
  });

  it('render folder tree with error', () => {
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
    setTimeout(() => {
      expect(screen.getByText(/User not found./)).toBeInTheDocument();
    }, 500);
  });
});
