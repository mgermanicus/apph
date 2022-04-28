import { render, screen } from '@testing-library/react';
import { UserProfileContainer } from '../../static/containers/UserProfileContainer';
import * as React from 'react';
import { triggerRequestFailure, triggerRequestSuccess } from '../utils/library';
import Cookies from 'universal-cookie';
import jwtDecode from 'jwt-decode';
import { JWS_TOKEN } from '../utils/token';

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
    render(<UserProfileContainer />);
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
    render(<UserProfileContainer />);
    //THEN
    const userName: HTMLDivElement = screen.getByText('Message: Token Expired');
    expect(userName.innerHTML).toEqual('Message: Token Expired');
  });
});
