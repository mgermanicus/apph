import AuthService from './AuthService';
import Cookies from 'universal-cookie';

const BASE_API_URL = process.env['REACT_APP_API_URL'];
const cookies = new Cookies();

export default class Server {
  static request(
    URL: string,
    requestOptions: RequestInit,
    successFunction: (body: string) => void | undefined,
    errorFunction: (error: string) => void,
    baseUrl = BASE_API_URL
  ) {
    return fetch(baseUrl + URL, requestOptions)
      .then(async (response) => {
        const body = await response.text();
        if (response.ok) {
          successFunction(body);
        } else {
          if (body === 'signin.error.credentials') {
            if (cookies.get('user') == undefined) {
              errorFunction('signin.error.credentials');
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
