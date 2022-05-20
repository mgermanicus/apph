import {
  JWS_TOKEN,
  triggerRequestFailure,
  triggerRequestSuccess
} from '../utils';
import Cookies from 'universal-cookie';
import jwtDecode from 'jwt-decode';
import { render, screen } from '@testing-library/react';
import * as React from 'react';
import { DisplayPhoto } from '../../static/components/DisplayPhoto';

describe("Display Folder's Photo Tests", () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('render display photo with success', () => {
    //GIVEN
    triggerRequestSuccess(
      '{"photoList":[{"id":1,"title":"photo","description":"photo test","creationDate":"2022-05-17T08:51:46.551+00:00","shootingDate":"2022-05-17T08:51:46.551+00:00","size":1300.0,"tags":[{"id":1,"version":0,"name":"tag"}],"url":"url","data":null,"format":null}]}'
    );
    const cookies = new Cookies();
    const decodedToken = jwtDecode(JWS_TOKEN);
    if (decodedToken !== null && typeof decodedToken === 'object') {
      cookies.set('user', { ...decodedToken, token: JWS_TOKEN });
    }
    //WHEN
    render(<DisplayPhoto selectedFolder="1" />);
    expect(screen.getByText(/photo/)).toBeInTheDocument();
  });

  it('render display photo with error', () => {
    //GIVEN
    triggerRequestFailure('{"message": "Le dossier n\'existe pas."}');
    const cookies = new Cookies();
    const decodedToken = jwtDecode(JWS_TOKEN);
    if (decodedToken !== null && typeof decodedToken === 'object') {
      cookies.set('user', { ...decodedToken, token: JWS_TOKEN });
    }
    //WHEN
    render(<DisplayPhoto selectedFolder="1" />);
    //THEN
    expect(screen.getByText(/Le dossier n'existe pas./)).toBeInTheDocument();
  });
});
