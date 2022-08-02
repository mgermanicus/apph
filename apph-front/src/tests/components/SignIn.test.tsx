import { SignIn } from '../../static/components/SignIn';
import * as React from 'react';
import {
  clickButton,
  fillPassword,
  fillText,
  JWS_TOKEN,
  triggerRequestFailure,
  triggerRequestSuccess
} from '../utils';
import cryptoJS from 'crypto-js';
import Cookies from 'universal-cookie';
import { renderWithWrapper } from '../../setupTests';

describe('Tests du composant SignIn.tsx', () => {
  const cookies = new Cookies();
  beforeEach(() => {
    jest.clearAllMocks();
    cookies.remove('user');
    Object.defineProperty(window, 'location', {
      configurable: true,
      value: { reload: jest.fn() }
    });
  });

  it('checks when the server sends an acknowledgment', () => {
    //GIVEN
    cryptoJS.SHA256('P@ssW0rd').toString = jest.fn(() => 'P@ssW0rd');
    triggerRequestSuccess(JWS_TOKEN);
    renderWithWrapper(<SignIn />);
    //WHEN
    fillText(/user.email/, 'test@viseo.com');
    fillPassword(/user.password/, 'P@ssW0rd');
    clickButton(/signin.login/);
    //THEN
    expect(cookies.get('user')).toStrictEqual({
      token: JWS_TOKEN
    });
  });

  it('checks when the server sends a failure', () => {
    //GIVEN
    cryptoJS.SHA256('P@ssW0rd').toString = jest.fn(() => 'P@ssW0rd');
    triggerRequestFailure('Test error');
    renderWithWrapper(<SignIn />);
    //WHEN
    fillText(/user.email/, 'test@viseo.com');
    fillPassword(/user.password/, 'P@ssW0rd');
    clickButton(/signin.login/);
    //THEN
    expect(cookies.get('user')).toStrictEqual(undefined);
  });

  it('checks when email fail', () => {
    //GIVEN
    cryptoJS.SHA256('P@ssW0rd').toString = jest.fn(() => 'P@ssW0rd');
    triggerRequestSuccess(JWS_TOKEN);
    renderWithWrapper(<SignIn />);
    //WHEN
    fillText(/user.email/, 'bad@email');
    fillPassword(/user.password/, 'P@ssW0rd');
    clickButton(/signin.login/);
    //THEN
    expect(cookies.get('user')).toStrictEqual(undefined);
  });
});
