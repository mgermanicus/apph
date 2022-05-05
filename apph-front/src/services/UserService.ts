import Server from './Server';
import { IEditedUser } from '../utils/types';
import cryptoJS from 'crypto-js';
import { getTokenHeader } from '../utils';

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
    { password, ...rest }: IEditedUser,
    handleSuccess: (user: string) => void,
    handleError: (errorMessage: string) => void
  ) {
    const requestOptions = {
      method: 'PUT',
      headers: getTokenHeader(),
      body: JSON.stringify({
        ...(password && { password: cryptoJS.SHA256(password).toString() }),
        ...rest
      })
    };

    return Server.request(
      `/user/edit/`,
      requestOptions,
      handleSuccess,
      handleError
    );
  }
}
