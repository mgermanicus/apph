import { IFolder, IMessage } from '../utils';
import Server from './Server';
import Cookies from 'universal-cookie';

const cookies = new Cookies();

export class FolderService {
  static getFolders(
    handleSuccess: (folder: IFolder) => void,
    handleError: (errorMessage: string) => void,
    parentFolder: string
  ) {
    const user = cookies.get('user');
    const requestOptions = {
      method: 'Get',
      headers: {
        'Content-Type': 'application/json',
        Authorization: 'Bearer ' + user.token
      }
    };
    const successFunction = (folder: string) => {
      handleSuccess(JSON.parse(folder));
    };
    const errorFunction = (errorMessage: string) => {
      handleError(JSON.parse(errorMessage).message);
    };
    return Server.request(
      `/folder/${parentFolder}`,
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
        Authorization: 'Bearer ' + user.token
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

  static moveFolder(
    folderToBeMoved: string,
    moveTo: string,
    handleSuccess: (message: { message: string }) => void,
    handleError: (errorMessage: IMessage) => void
  ) {
    const user = cookies.get('user');
    const requestOptions = {
      method: 'Post',
      headers: {
        'Content-Type': 'application/json',
        Authorization: 'Bearer ' + user.token
      },
      body: JSON.stringify({
        folderToBeMoved,
        moveTo
      })
    };
    const successFunction = (message: string) => {
      handleSuccess(JSON.parse(message));
    };
    const errorFunction = (errorMessage: string) => {
      handleError(JSON.parse(errorMessage));
    };
    return Server.request(
      '/folder/move',
      requestOptions,
      successFunction,
      errorFunction
    );
  }
}
