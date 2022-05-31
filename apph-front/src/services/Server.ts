import AuthService from './AuthService';

const BASE_API_URL = process.env['REACT_APP_API_URL'];

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
            AuthService.logout();
          }
          errorFunction(body);
        }
      })
      .catch((error) => {
        errorFunction('{"message":"Échec de connexion au serveur"}');
        console.error(error);
      });
  }
}
