import { IFolder } from '../utils/types/Folder';
import Server from './Server';

export class FolderService {
  static getFolders(
    id: number,
    handleSuccess: (folder: IFolder) => void,
    handleError: (errorMessage: string) => void
  ) {
    const URL = `/folder/${id}`;
    const requestOptions = {
      method: 'Get',
      headers: {
        'Content-Type': 'application/json'
      }
    };
    const successFunction = (folder: string) => {
      handleSuccess(JSON.parse(folder));
    };
    const errorFunction = (errorMessage: string) => {
      handleError(JSON.parse(errorMessage).message);
    };
    return Server.request(URL, requestOptions, successFunction, errorFunction);
  }
}
