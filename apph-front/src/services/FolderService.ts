import { IFolder } from '../utils/types/Folder';
import Server from './Server';
import Cookies from 'universal-cookie';

const cookies = new Cookies();

export class FolderService {
  static getFolders(
    handleSuccess: (folder: IFolder) => void,
    handleError: (errorMessage: string) => void
  ) {
    const user = cookies.get('user');
    const requestOptions = {
      method: 'Get',
      headers: {
        'Content-Type': 'application/json',
        Authentication: user.token
      }
    };
    const successFunction = (folder: string) => {
      handleSuccess(JSON.parse(folder));
    };
    const errorFunction = (errorMessage: string) => {
      handleError(JSON.parse(errorMessage).message);
    };
    return Server.request(
      `/folder/`,
      requestOptions,
      successFunction,
      errorFunction
    );
  }

  static createFolder(
    name: string,
    parentFolderId: string,
    handleSuccess: (folder: IFolder) => void,
    handleError: (errorMessage: string) => void
  ) {
    const user = cookies.get('user');
    const requestOptions = {
      method: 'Post',
      headers: {
        'Content-Type': 'application/json',
        Authentication: user.token
      },
      body: JSON.stringify({
        name,
        parentFolderId
      })
    };
    const successFunction = (folder: string) => {
      handleSuccess(JSON.parse(folder));
    };
    const errorFunction = (errorMessage: string) => {
      handleError(JSON.parse(errorMessage).message);
    };
    return Server.request(
      `/folder/add`,
      requestOptions,
      successFunction,
      errorFunction
    );
  }
}
