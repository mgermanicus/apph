import { SignUp } from '../../static/components/SignUp';
import * as React from 'react';
import { render } from '@testing-library/react';
import {
  clickButton,
  fillPassword,
  fillText,
  JWS_TOKEN,
  triggerRequestFailure,
  triggerRequestSuccess
} from '../utils';
import cryptoJS from 'crypto-js';
import { useNavigate } from 'react-router-dom';
import { screen } from '@testing-library/dom';

jest.mock('react-i18next', () => ({
  // this mock makes sure any components using the translate hook can use it without a warning being shown
  useTranslation: () => {
    return {
      t: (str: string) => str
    };
  }
}));
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
    fillText(/user.email/, 'test@viseo.com');
    fillPassword(/user.password /, 'P@ssW0rd');
    fillPassword(/user.passwordConfirmation/, 'P@ssW0rd');
    fillText(/user.firstName/, 'Bob');
    fillText(/user.lastName/, 'Dupont');
    clickButton(/signup.create/);
    //THEN
    expect(useNavigate()).toBeCalled();
  });

  it('checks when the server sends an error if invalid email', () => {
    //GIVEN
    cryptoJS.SHA256('P@ssW0rd').toString = jest.fn(() => 'P@ssW0rd');
    triggerRequestSuccess(JWS_TOKEN);
    render(<SignUp />);
    //WHEN
    fillText(/user.email/, 'bad@Email');
    fillPassword(/user.password /, 'P@ssW0rd');
    fillPassword(/user.passwordConfirmation/, 'P@ssW0rd');
    fillText(/user.firstName/, 'Bob');
    fillText(/user.lastName/, 'Dupont');
    clickButton(/signup.create/);
    //THEN
    expect(screen.getByText(/signup.error.email/)).toBeInTheDocument();
    expect(useNavigate()).not.toBeCalled();
  });

  it('checks when the server sends an error if invalid confirm password', () => {
    //GIVEN
    cryptoJS.SHA256('P@ssW0rd').toString = jest.fn(() => 'P@ssW0rd');
    triggerRequestSuccess(JWS_TOKEN);
    render(<SignUp />);
    //WHEN
    fillText(/user.email/, 'test@viseo.com');
    fillPassword(/user.password /, 'P@ssW0rd');
    fillPassword(/user.passwordConfirmation/, 'NotSamePassword');
    fillText(/user.firstName/, 'Bob');
    fillText(/user.lastName/, 'Dupont');
    clickButton(/signup.create/);
    //THEN
    expect(screen.getByText(/signup.error.password/)).toBeInTheDocument();
    expect(useNavigate()).not.toBeCalled();
  });

  it('checks when the server sends an error request fail', () => {
    //GIVEN
    cryptoJS.SHA256('P@ssW0rd').toString = jest.fn(() => 'P@ssW0rd');
    triggerRequestFailure('Test error');
    render(<SignUp />);
    //WHEN
    fillText(/user.email/, 'test@viseo.com');
    fillPassword(/user.password /, 'P@ssW0rd');
    fillPassword(/user.passwordConfirmation/, 'P@ssW0rd');
    fillText(/user.firstName/, 'Bob');
    fillText(/user.lastName/, 'Dupont');
    clickButton(/signup.create/);
    //THEN
    expect(screen.getByText(/Test error/)).toBeInTheDocument();
    expect(useNavigate()).not.toBeCalled();
  });
});
