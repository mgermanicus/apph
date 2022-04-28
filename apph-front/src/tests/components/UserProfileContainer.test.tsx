import { render, screen } from '@testing-library/react';
import { UserProfilePage } from '../../static/pages/UserProfilePage';
import * as React from 'react';
import {
  JWS_TOKEN,
  triggerRequestFailure,
  triggerRequestSuccess
} from '../utils';
import Cookies from 'universal-cookie';
import jwtDecode from 'jwt-decode';

describe('UserAvatar Component Tests', () => {
  it('render when user is connected', () => {
    //GIVEN
    triggerRequestSuccess(
      '{"login":"Elie","firstname":"Elie","lastname":"RAVASSE"}'
    );
    const cookies = new Cookies();
    const decodedToken = jwtDecode(JWS_TOKEN);
    if (decodedToken !== null && typeof decodedToken === 'object') {
      cookies.set('user', { ...decodedToken, token: JWS_TOKEN });
    }
    //WHEN
    render(<UserProfilePage />);
    //THEN
    const userName: HTMLSpanElement = screen.getByText('Elie RAVASSE');
    expect(userName.innerHTML).toEqual('Elie RAVASSE');
  });

  it('render error', () => {
    //GIVEN
    triggerRequestFailure('Token Expired');
    const cookies = new Cookies();
    const decodedToken = jwtDecode(JWS_TOKEN);
    if (decodedToken !== null && typeof decodedToken === 'object') {
      cookies.set('user', { ...decodedToken, token: JWS_TOKEN });
    }
    //WHEN
    render(<UserProfilePage />);
    //THEN
    const userName: HTMLDivElement = screen.getByText('Message: Token Expired');
    expect(userName.innerHTML).toEqual('Message: Token Expired');
  });
});
