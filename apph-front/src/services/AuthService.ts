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
      const decodedToken = jwtDecode(jws) as {
        id: number;
        exp: number;
      } & IUser;
      const user: IUser = {
        firstname: '',
        lastname: '',
        login: ''
      };
      if (typeof decodedToken === 'object') {
        user.login = decodedToken.login;
        user.lastname = decodedToken.lastname;
        user.firstname = decodedToken.firstname;
        cookies.set(
          'user',
          { token: jws },
          { expires: new Date(decodedToken.exp * 1000) }
        );
      }
      handleSuccess(user);
    };
    const errorFunction = (errorMessage: string) => {
      handleError(errorMessage);
    };
    return Server.request(URL, requestOptions, successFunction, errorFunction);
  }

  static async signUp(
    email: string,
    password: string,
    firstName: string,
    lastName: string,
    handleSuccess: () => void,
    handleError: (errorMessage: string) => void
  ) {
    const URL = `/auth/signUp`;
    const requestOptions = {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        email,
        password: cryptoJS.SHA256(password).toString(),
        firstName,
        lastName
      })
    };
    return Server.request(URL, requestOptions, handleSuccess, handleError);
  }

  static logout = () => {
    cookies.remove('user');
  };

  static getToken() {
    return cookies.get('user')?.token;
  }

  static updateUserCookie = (newToken: string) => {
    const decodedToken = jwtDecode(newToken) as {
      id: number;
      exp: number;
    } & IUser;
    cookies.set(
      'user',
      { token: newToken },
      { expires: new Date(decodedToken.exp * 1000) }
    );
  };

  static isTokenValid() {
    return !!AuthService.getToken();
  }

  static getUserLoginByToken() {
    return jwtDecode(AuthService.getToken()) as IUser;
  }
}
