import cryptoJS from 'crypto-js';
import jwtDecode from 'jwt-decode';
import Server from './Server';
import Cookies from 'universal-cookie';

const cookies = new Cookies();

export default class AuthService {
  static signIn(
    email: string,
    password: string,
    handleSuccess: () => void,
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

  static logout = () => {
    cookies.remove('user');
    location.reload();
  };

  static getCurrentUser() {
    return cookies.get('user');
  }
}
