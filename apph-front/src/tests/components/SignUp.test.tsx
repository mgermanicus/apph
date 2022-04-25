import { SignUp } from '../../static/components/SignUp';
import * as React from 'react';
import { render } from '@testing-library/react';
import {
  clickButton,
  fillPassword,
  fillText,
  triggerRequestSuccess
} from '../utils/library';
import cryptoJS from 'crypto-js';

describe('Tests du composant SignUp.tsx', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('checks when the server sends an acknowledgment', () => {
    //GIVEN
    cryptoJS.SHA256('P@ssW0rd').toString = jest.fn(() => 'P@ssW0rd');
    triggerRequestSuccess(
      'eyJhbGciOiJIUzI1NiJ9.eyJsb2dpbiI6IkVsaWUifQ.slvgzwhi13LHv_KeKsRKpKMoulz7qVVU0A-LCgCPPRk'
    );
    render(<SignUp />);
    //WHEN
    fillText(/Email/, 'test@viseo.com');
    fillPassword(/Mot de passe/, 'P@ssW0rd');
    fillPassword(/Prénom/, 'Bob');
    fillPassword(/Nom/, 'Dupont');
    clickButton(/Créer votre compte/);
    //THEN
    //no assert needed just check no error was thrown
  });
});
