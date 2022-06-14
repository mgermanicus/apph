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

jest.mock('react-i18next', () => ({
  // this mock makes sure any components using the translate hook can use it without a warning being shown
  useTranslation: () => {
    return {
      t: (str: string) => str
    };
  }
}));

describe("Display Folder's Photo Tests", () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('render display photo with success', () => {
    //GIVEN
    triggerRequestSuccess(
      '{"photoList":[{"id":1,"title":"photo","description":"photo test","creationDate":"2022-05-17T08:51:46.551+00:00","shootingDate":"2022-05-17T08:51:46.551+00:00","size":1300.0,"tags":[{"id":1,"version":0,"name":"tag"}],"url":"url","data":null,"format":".png"}]}'
    );
    const cookies = new Cookies();
    const decodedToken = jwtDecode(JWS_TOKEN);
    if (decodedToken !== null && typeof decodedToken === 'object') {
      cookies.set('user', { ...decodedToken, token: JWS_TOKEN });
    }
    //WHEN
    render(<DisplayPhoto rootFolder="0" selectedFolder="1" />);
    setTimeout(
      () => expect(screen.getByText(/photo.png/)).toBeInTheDocument(),
      500
    );
  });

  it('render display photo with error', () => {
    //GIVEN
    triggerRequestFailure('{"message": "folder.error.notExist"}');
    const cookies = new Cookies();
    const decodedToken = jwtDecode(JWS_TOKEN);
    if (decodedToken !== null && typeof decodedToken === 'object') {
      cookies.set('user', { ...decodedToken, token: JWS_TOKEN });
    }
    //WHEN
    render(<DisplayPhoto rootFolder={'0'} selectedFolder="1" />);
    //THEN
    expect(screen.getByText(/folder.error.notExist/)).toBeInTheDocument();
  });
});
