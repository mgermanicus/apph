import cryptoJS from 'crypto-js';
import jwtDecode from 'jwt-decode';
import Server from './Server';

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
        localStorage.setItem('user', JSON.stringify(decodedToken));
        localStorage.setItem('token', jws);
      }
      handleSuccess();
    };
    const errorFunction = (errorMessage: string) => {
      handleError(errorMessage);
    };
    return Server.request(URL, requestOptions, successFunction, errorFunction);
  }

  static logout = () => {
    localStorage.removeItem('user');
    localStorage.removeItem('token');
  };

  static getCurrentUser() {
    return localStorage.getItem('user');
  }
}
