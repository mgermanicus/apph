import cryptoJS from 'crypto-js';
import jwtDecode from 'jwt-decode';
import Server from './Server';
import Cookies from 'universal-cookie';
import { IUser } from '../utils';

const cookies = new Cookies();

export default class AuthService {
  static signIn(
    email: string,
    password: string,
    handleSuccess: (user: IUser) => void,
    handleError: (errorMessage: string) => void
  ) {
    const URL = `/auth/signIn`;
    const requestOptions = {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        email,
        password: cryptoJS.SHA256(password).toString()
      })
    };
    const successFunction = (jws: string) => {
      const decodedToken = jwtDecode(jws) as { login: string; exp: number };
      const user: IUser = {
        firstname: '',
        lastname: '',
        login: ''
      };
      if (typeof decodedToken === 'object') {
        user.login = decodedToken.login;
        cookies.set('user', { token: jws });
      }
      handleSuccess(user);
    };
    const errorFunction = (errorMessage: string) => {
      handleError(errorMessage);
    };
    return Server.request(URL, requestOptions, successFunction, errorFunction);
  }

  static logout = () => {
    cookies.remove('user');
  };

  static getToken() {
    return cookies.get('user')?.token;
  }

  static isTokenValid() {
    const jwt = AuthService.getToken();
    if (jwt) {
      return (
        (jwtDecode(AuthService.getToken()) as { exp: number })?.exp * 1000 >
        Date.now()
      );
    }
    return false;
  }

  static getUserLoginByToken() {
    return jwtDecode(AuthService.getToken()) as IUser;
  }
}
