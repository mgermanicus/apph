import cryptoJS from 'crypto-js';
import jwtDecode from 'jwt-decode';
import Cookies from 'universal-cookie';
import Server from './Server';
import { IEditedUser, IUser } from '../utils/types';

const cookies = new Cookies();

export default class UserService {
  static signIn(
    email: string,
    password: string,
    handleSuccess: () => void,
    handleError: (errorMessage: string) => void
  ) {
    const URL = '/auth/signIn';
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
      const decodedToken = jwtDecode(jws);
      if (decodedToken !== null && typeof decodedToken === 'object') {
        cookies.set('user', { ...decodedToken, token: jws });
      }
      handleSuccess();
    };
    const errorFunction = (errorMessage: string) => {
      handleError(errorMessage);
    };
    return Server.request(URL, requestOptions, successFunction, errorFunction);
  }

  static getUser(
    handleSuccess: (user: string) => void,
    handleError: (errorMessage: string) => void
  ) {
    const token = cookies.get('user');
    const requestOptions = {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
        Authentication: token.token
      }
    };

    return Server.request(`/user/`, requestOptions, handleSuccess, handleError);
  }

  static editUser(
    { password, ...rest }: IEditedUser,
    handleSuccess: (user: string) => void,
    handleError: (errorMessage: string) => void
  ) {
    const token = cookies.get('user');
    const requestOptions = {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',
        Authentication: token.token
      },
      body: JSON.stringify({
        ...(password && { password: cryptoJS.SHA256(password).toString() }),
        ...rest
      })
    };

    return Server.request(
      `/user/edit`,
      requestOptions,
      handleSuccess,
      handleError
    );
  }

  static updateCookies(user: IUser) {
    const token = cookies.get('user').token;
    cookies.set('user', { ...user, token });
  }
}
