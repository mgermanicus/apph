import Server from './Server';
import { getTokenHeader, IUser, IUserRequest, IUserTable } from '../utils';
import cryptoJS from 'crypto-js';
import Cookies from 'universal-cookie';

const cookies = new Cookies();

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
    { password, ...rest }: IUserRequest,
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

  static getUserList(
    handleSuccess: (list: IUserTable[]) => void,
    handleError: (errorMessage: string) => void
  ) {
    const URL = '/admin/users';
    const userInfos = cookies.get('user');
    const requestOptions = {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
        Authorization: 'Bearer ' + userInfos?.token
      }
    };
    const successFunction = (val: string) => {
      handleSuccess(
        JSON.parse(val).map((user: IUser) => {
          return {
            id: user.login,
            firstname: user.firstname,
            lastname: user.lastname,
            email: user.login
          } as IUserTable;
        })
      );
    };
    const errorFunction = (errorMessage: string) => {
      handleError(JSON.parse(errorMessage).message);
    };
    return Server.request(URL, requestOptions, successFunction, errorFunction);
  }

  static getContact(
    handleSuccess: (list: IUser[]) => void,
    handleError: (errorMessage: string) => void
  ) {
    const URL = '/user/contact/get';
    const userInfos = cookies.get('user');
    const requestOptions = {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
        Authorization: 'Bearer ' + userInfos?.token
      }
    };
    const successFunction = (val: string) => {
      handleSuccess(JSON.parse(val).userList);
    };
    const errorFunction = (errorMessage: string) => {
      handleError(JSON.parse(errorMessage).message);
    };
    return Server.request(URL, requestOptions, successFunction, errorFunction);
  }

  static addContact(
    email: string,
    handleSuccess: (list: IUser[]) => void,
    handleError: (errorMessage: string) => void
  ) {
    const URL = '/user/contact/add';
    const userInfos = cookies.get('user');
    const requestOptions = {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        Authorization: 'Bearer ' + userInfos?.token
      },
      body: JSON.stringify({
        email
      })
    };
    const successFunction = (val: string) => {
      handleSuccess(JSON.parse(val).userList);
    };
    const errorFunction = (errorMessage: string) => {
      handleError(JSON.parse(errorMessage).message);
    };
    return Server.request(URL, requestOptions, successFunction, errorFunction);
  }

  static deleteUser(
    email: string,
    handleSuccess: () => void,
    handleError: (errorMessage: string) => void
  ) {
    const URL = '/admin/deleteUser';
    const userInfos = cookies.get('user');
    const requestOptions = {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        Authorization: 'Bearer ' + userInfos?.token
      },
      body: JSON.stringify({
        email
      })
    };
    const successFunction = () => {
      handleSuccess();
    };
    const errorFunction = (errorMessage: string) => {
      handleError(JSON.parse(errorMessage).message);
    };
    return Server.request(URL, requestOptions, successFunction, errorFunction);
  }
}
