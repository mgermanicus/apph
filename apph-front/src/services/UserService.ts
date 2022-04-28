import Server from './Server';
import { authHeader } from './AuthHeader';

export default class UserService {
  static getUser(
    handleSuccess: (user: string) => void,
    handleError: (errorMessage: string) => void
  ) {
    const requestOptions = {
      method: 'GET',
      headers: authHeader()
    };
    return Server.request(`/user/`, requestOptions, handleSuccess, handleError);
  }
}
