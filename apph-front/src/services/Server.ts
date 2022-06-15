import AuthService from './AuthService';
import Cookies from 'universal-cookie';

const BASE_API_URL = process.env['REACT_APP_API_URL'];
const cookies = new Cookies();

export default class Server {
  static request(
    URL: string,
    requestOptions: RequestInit,
    successFunction: (body: string) => void | undefined,
    errorFunction: (error: string) => void
  ) {
    return fetch(BASE_API_URL + URL, requestOptions)
      .then(async (response) => {
        const body = await response.text();
        if (response.ok) {
          successFunction(body);
        } else {
          if (body === 'Token invalide') {
            if (cookies.get('user') == undefined) {
              errorFunction('Login ou mot de passe incorrect.');
            } else {
              AuthService.logout();
              errorFunction(body);
            }
          } else {
            errorFunction(body);
          }
        }
      })
      .catch((error) => {
        errorFunction('signin.error.serverConnection');
        console.error(error);
      });
  }
}
