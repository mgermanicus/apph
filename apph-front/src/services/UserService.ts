import cryptoJS from 'crypto-js';
import jwtDecode from 'jwt-decode';
import Cookies from 'universal-cookie';
import Server from './Server';

const API_URL = process.env['REACT_APP_API_URL'];
const cookies = new Cookies();

export default class UserService {
  static signIn(
    email: string,
    password: string,
    handleSuccess: () => void,
    handleError: (errorMessage: string) => void
  ) {
    const URL = `${API_URL}/auth/signIn`;
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
    const { login, ...token } = cookies.get('user');
    const requestOptions = {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
        Authentication: token.token
      }
    };

    const successFunction = (user: string) => {
      if (user !== null) {
        handleSuccess(user);
      }
    };
    const errorFunction = (errorMessage: string) => {
      handleError(errorMessage);
    };

    return Server.request(
      `${API_URL}/user/${login}`,
      requestOptions,
      successFunction,
      errorFunction
    );
  }
}
