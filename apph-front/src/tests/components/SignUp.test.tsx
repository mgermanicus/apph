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
import { screen } from '@testing-library/dom';

const mockedUseNavigate = jest.fn();
jest.mock('react-router-dom', () => ({
  ...jest.requireActual('react-router-dom'),
  useNavigate: () => mockedUseNavigate
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
    expect(mockedUseNavigate).toBeCalled();
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
    expect(mockedUseNavigate).not.toBeCalled();
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
    expect(mockedUseNavigate).not.toBeCalled();
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
    expect(mockedUseNavigate).not.toBeCalled();
  });
});
