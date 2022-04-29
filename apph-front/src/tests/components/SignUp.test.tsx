import { SignUp } from '../../static/components/SignUp';
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
import { useNavigate } from 'react-router-dom';

const mockedUsedNavigate = jest.fn();
jest.mock('react-router-dom', () => ({
  ...jest.requireActual('react-router-dom'),
  useNavigate: () => mockedUsedNavigate
}));

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
    fillPassword(/confirmer le mot de passe/, 'P@ssW0rd');
    fillPassword(/Prénom/, 'Bob');
    fillPassword(/Nom/, 'Dupont');
    clickButton(/Créer votre compte/);
    //THEN
    expect(useNavigate()).toBeCalled();
  });

  it('checks when the server sends an error', () => {
    //GIVEN
    cryptoJS.SHA256('P@ssW0rd').toString = jest.fn(() => 'P@ssW0rd');
    triggerRequestFailure('Test error');
    render(<SignUp />);
    //WHEN
    fillText(/Email/, 'test@viseo.com');
    fillPassword(/Mot de passe/, 'P@ssW0rd');
    fillPassword(/confirmer le mot de passe/, 'P@ssW0rd');
    fillPassword(/Prénom/, 'Bob');
    fillPassword(/Nom/, 'Dupont');
    clickButton(/Créer votre compte/);
    //THEN
    expect(useNavigate()).not.toBeCalled();
  });
});
