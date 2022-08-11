import { SignIn } from '../../static/components/SignIn';
import * as React from 'react';
import { fireEvent, getByText, render, screen } from '@testing-library/react';
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
import { renderWithWrapper } from '../utils';
import { wrapper } from '../utils/components/CustomWrapper';

jest.mock('react-i18next', () => ({
  // this mock makes sure any components using the translate hook can use it without a warning being shown
  useTranslation: () => {
    return {
      t: (str: string) => str,
      i18n: {
        language: 'fr'
      }
    };
  }
}));

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

  it('Resetting password with bad email', () => {
    //GIVEN
    renderWithWrapper(<SignIn />);
    const linkButton = screen.getByText(/signin.forgottenPassword/);
    //WHEN
    fireEvent.click(linkButton, { button: 0 });
    //THEN
    expect(screen.getByText(/user.forgottenPassword/)).toBeInTheDocument();
    //WHEN
    const inputEmail = screen.getByTestId(/emailReset/).querySelector('input');
    const submit = screen.getByText(/user.resetPassword/);
    fireEvent.change(inputEmail as Element, { target: { value: 'test' } });
    fireEvent.click(submit, { button: 0 });
    //THEN
    expect(screen.getByText(/user.error.email/)).toBeInTheDocument();
  });
  it('Error during resetting', () => {
    //GIVEN
    triggerRequestFailure('Error during resetting');
    renderWithWrapper(<SignIn />);
    const linkButton = screen.getByText(/signin.forgottenPassword/);
    //WHEN
    fireEvent.click(linkButton, { button: 0 });
    //THEN
    expect(screen.getByText(/user.forgottenPassword/)).toBeInTheDocument();
    //WHEN
    const inputEmail = screen.getByTestId(/emailReset/).querySelector('input');
    const submit = screen.getByText(/user.resetPassword/);
    fireEvent.change(inputEmail as Element, {
      target: { value: 'test@viseo.com' }
    });
    fireEvent.click(submit, { button: 0 });
    //THEN
    expect(screen.getByText(/Error during resetting/)).toBeInTheDocument();
  });
  it('Resetting password with good email', () => {
    //GIVEN
    triggerRequestSuccess('');
    renderWithWrapper(<SignIn />);
    const linkButton = screen.getByText(/signin.forgottenPassword/);
    //WHEN
    fireEvent.click(linkButton, { button: 0 });
    //THEN
    expect(screen.getByText(/user.forgottenPassword/)).toBeInTheDocument();
    //WHEN
    const inputEmail = screen.getByTestId(/emailReset/).querySelector('input');
    const submit = screen.getByText(/user.resetPassword/);
    fireEvent.change(inputEmail as Element, {
      target: { value: 'test@viseo.com' }
    });
    fireEvent.click(submit, { button: 0 });
    //THEN
    expect(
      screen.getByText(/user.emailSend test@viseo.com/)
    ).toBeInTheDocument();
  });
});
