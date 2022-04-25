import {
  SignIn,
  isConnected,
  resetConnected
} from '../../static/components/SignIn';
import * as React from 'react';
import { render } from '@testing-library/react';
import {
  clickButton,
  fillPassword,
  fillText,
  triggerRequestFailure,
  triggerRequestSuccess
} from '../utils/library';
import cryptoJS from 'crypto-js';

describe('Tests du composant SignIn.tsx', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    resetConnected();
  });

  it('checks when the server sends an acknowledgment', () => {
    //GIVEN
    cryptoJS.SHA256('P@ssW0rd').toString = jest.fn(() => 'P@ssW0rd');
    triggerRequestSuccess(
      'eyJhbGciOiJIUzI1NiJ9.eyJsb2dpbiI6IkVsaWUifQ.slvgzwhi13LHv_KeKsRKpKMoulz7qVVU0A-LCgCPPRk'
    );
    render(<SignIn />);
    //WHEN
    fillText(/Adresse email/, 'test@viseo.com');
    fillPassword(/Mot de passe/, 'P@ssW0rd');
    clickButton(/Connexion/);
    //THEN
    expect(isConnected()).toBe(true);
  });

  it('checks when the server sends a failure', () => {
    //GIVEN
    cryptoJS.SHA256('P@ssW0rd').toString = jest.fn(() => 'P@ssW0rd');
    triggerRequestFailure('Test error');
    render(<SignIn />);
    //WHEN
    fillText(/Adresse email/, 'test@viseo.com');
    fillPassword(/Mot de passe/, 'P@ssW0rd');
    clickButton(/Connexion/);
    //THEN
    expect(isConnected()).toBe(false);
  });
});
