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
    folderIdToBeMoved: string,
    destinationFolderId: string,
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
        folderIdToBeMoved,
        destinationFolderId
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

  static downloadFolder(
    id: number,
    handleSuccess: (folder: IFolder) => void,
    handleError: (errorMessage: IMessage) => void
  ) {
    const URL = `/folder/download`,
      userInfos = cookies.get('user'),
      requestOptions = {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          Authorization: 'Bearer ' + userInfos?.token
        },
        body: JSON.stringify({
          id
        })
      };
    const successFunction = (folder: string) => {
      handleSuccess(JSON.parse(folder));
    };
    const errorFunction = (errorMessage: string) => {
      handleError(JSON.parse(errorMessage));
    };
    return Server.request(URL, requestOptions, successFunction, errorFunction);
  }

  static deleteFolder(
    handleSuccess: (successMessage: string) => void,
    handleError: (errorMessage: string) => void,
    id: string,
    destinationFolderId?: string
  ) {
    const user = cookies.get('user');
    const requestOptions = {
      method: 'Delete',
      headers: {
        'Content-Type': 'application/json',
        Authorization: 'Bearer ' + user.token
      },
      body: JSON.stringify({
        id,
        destinationFolderId
      })
    };
    const successFunction = (message: string) => {
      handleSuccess(JSON.parse(message).message);
    };
    const errorFunction = (errorMessage: string) => {
      handleError(JSON.parse(errorMessage).message);
    };
    return Server.request(
      `/folder/delete`,
      requestOptions,
      successFunction,
      errorFunction
    );
  }
}
