import Server from './Server';
import { getTokenHeader } from '../utils/token';

export default class UserService {
  static getUser(
    handleSuccess: (user: string) => void,
    handleError: (errorMessage: string) => void
  ) {
    const requestOptions = {
      method: 'GET',
      headers: getTokenHeader()
    };
    return Server.request(`/user/`, requestOptions, handleSuccess, handleError);
  }

  static editUser(
    firstname?: string,
    lastname?: string,
    login?: string,
    password?: string
  ) {
    // TODO send to server & edit cookies
    return null;
  }
}
