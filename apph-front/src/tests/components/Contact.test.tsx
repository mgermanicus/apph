import { render, screen } from '@testing-library/react';
import { Contact } from '../../static/components/Contact';
import {
  clickButton,
  fillText,
  JWS_TOKEN,
  triggerRequestFailure,
  triggerRequestSuccess
} from '../utils';
import Cookies from 'universal-cookie';
import jwtDecode from 'jwt-decode';

jest.mock('react-i18next', () => ({
  // this mock makes sure any components using the translate hook can use it without a warning being shown
  useTranslation: () => {
    return {
      t: (str: string) => str
    };
  }
}));

describe('Contact Component Tests', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('render contact', () => {
    //GIVEN
    triggerRequestSuccess(
      '{"userList":[{"login":"wassim@viseo.com","firstname":"Wassim","lastname":"BOUHTOUT"}]}'
    );
    const cookies = new Cookies();
    const decodedToken = jwtDecode(JWS_TOKEN);
    if (decodedToken !== null && typeof decodedToken === 'object') {
      cookies.set('user', { ...decodedToken, token: JWS_TOKEN });
    }
    //WHEN
    render(<Contact />);
    //THEN
    expect(screen.getByText(/wassim@viseo.com/)).toBeInTheDocument();
  });

  it('render contact error', () => {
    //GIVEN
    triggerRequestFailure('{ "message": "Error" }');
    const cookies = new Cookies();
    const decodedToken = jwtDecode(JWS_TOKEN);
    if (decodedToken !== null && typeof decodedToken === 'object') {
      cookies.set('user', { ...decodedToken, token: JWS_TOKEN });
    }
    //WHEN
    render(<Contact />);
    //THEN
    expect(screen.getByText(/Error/)).toBeInTheDocument();
  });

  it('add contact', () => {
    //GIVEN
    triggerRequestSuccess('{"userList":[]}');
    const cookies = new Cookies();
    const decodedToken = jwtDecode(JWS_TOKEN);
    if (decodedToken !== null && typeof decodedToken === 'object') {
      cookies.set('user', { ...decodedToken, token: JWS_TOKEN });
    }
    render(<Contact />);
    triggerRequestSuccess(
      '{"userList":[{"login":"wassim@viseo.com","firstname":"Wassim","lastname":"BOUHTOUT"}]}'
    );
    //WHEN
    fillText(/user.contact.new/, 'wassim@viseo.com');
    clickButton(/add-contact/i);
    //THEN
    expect(screen.getByText(/wassim@viseo.com/)).toBeInTheDocument();
  });

  it('add contact with errors', () => {
    //GIVEN
    triggerRequestSuccess('{"userList":[]}');
    const cookies = new Cookies();
    const decodedToken = jwtDecode(JWS_TOKEN);
    if (decodedToken !== null && typeof decodedToken === 'object') {
      cookies.set('user', { ...decodedToken, token: JWS_TOKEN });
    }
    render(<Contact />);
    triggerRequestFailure('{ "message": "Error" }');
    //WHEN
    clickButton(/add-contact/i);
    //THEN
    expect(
      screen.getByText(/user.contact.error.fillInput/)
    ).toBeInTheDocument();
    //WHEN
    fillText(/user.contact.new/, 'wassim');
    clickButton(/add-contact/i);
    //THEN
    expect(screen.getByText(/user.contact.error.email/)).toBeInTheDocument();
    //WHEN
    fillText(/user.contact.new/, 'wassim@viseo.com');
    clickButton(/add-contact/i);
    //THEN
    expect(screen.getByText(/Error/)).toBeInTheDocument();
  });
});
