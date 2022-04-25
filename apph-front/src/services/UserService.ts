import cryptoJS from 'crypto-js';
import jwtDecode from 'jwt-decode';
import Cookies from 'universal-cookie';
import Server from './Server';

const API_URL = process.env['REACT_APP_API_URL'];
const cookies = new Cookies();

class UserService {
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

  static async signUp(
    email: string,
    password: string,
    firstName: string,
    lastName: string,
    handleSuccess: () => void,
    handleError: (errorMessage: string) => void
  ) {
    const URL = `${API_URL}/auth/signUp`;
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
    const successFunction = () => {
      handleSuccess();
    };
    const errorFunction = (errorMessage: string) => {
      handleError(errorMessage);
    };
    return Server.request(URL, requestOptions, successFunction, errorFunction);
  }
}

export default UserService;
