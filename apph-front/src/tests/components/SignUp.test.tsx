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
import { JWS_TOKEN } from '../utils/token';

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
    triggerRequestSuccess(JWS_TOKEN);
    render(<SignUp />);
    //WHEN
    fillText(/Email/, 'test@viseo.com');
    fillPassword(/Mot de passe/, 'P@ssW0rd');
    fillPassword(/confirmer le mot de passe/, 'P@ssW0rd');
    fillText(/Prénom/, 'Bob');
    fillText(/Nom/, 'Dupont');
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
    fillText(/Prénom/, 'Bob');
    fillText(/Nom/, 'Dupont');
    clickButton(/Créer votre compte/);
    //THEN
    expect(useNavigate()).not.toBeCalled();
  });
});
