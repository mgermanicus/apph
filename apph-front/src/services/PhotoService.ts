import Server from './Server';
import {
  imageFileCheck,
  IMessage,
  IPagination,
  IPhoto,
  ITable,
  ITag
} from '../utils';
import Cookies from 'universal-cookie';
import { IFilterPayload } from '../utils/types/Filter';

const cookies = new Cookies();
export default class PhotoService {
  static uploadImage(
    title: string,
    description: string,
    shootingDate: Date,
    imageFile: File,
    selectedTags: ITag[],
    folderId: string,
    handleSuccess: () => void,
    handleError: (errorMessage: string) => void
  ) {
    if (!imageFileCheck(imageFile, handleError)) return;
    const userInfos = cookies.get('user');
    const formData = new FormData();
    formData.append('file', imageFile);
    formData.append('tags', JSON.stringify(selectedTags));
    formData.append('title', title);
    formData.append('description', description);
    formData.append(
      'shootingDate',
      JSON.stringify(shootingDate.toLocaleString())
    );
    formData.append('folderId', folderId);
    const requestOptions = {
      method: 'POST',
      headers: {
        Authorization: 'Bearer ' + userInfos?.token
      },
      body: formData
    };
    const errorFunction = (errorMessage: string) => {
      handleError(JSON.parse(errorMessage).message);
    };
    return Server.request(
      `/photo/upload`,
      requestOptions,
      handleSuccess,
      errorFunction
    );
  }

  static getData(
    pageSize: number,
    page: number,
    handleSuccess: (pagination: IPagination) => void,
    handleError: (errorMessage: string) => void,
    filterList?: IFilterPayload[]
  ) {
    const URL = '/photo/infos';
    const userInfos = cookies.get('user');
    const requestOptions = {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        Authorization: 'Bearer ' + userInfos?.token
      },
      body: JSON.stringify({
        pageSize: encodeURIComponent(pageSize),
        page: encodeURIComponent(page),
        filterList
      })
    };
    const successFunction = (val: string) => {
      handleSuccess(JSON.parse(val));
    };
    const errorFunction = (errorMessage: string) => {
      handleError(JSON.parse(errorMessage).message);
    };
    return Server.request(URL, requestOptions, successFunction, errorFunction);
  }

  static getFolderPhotos(
    folderId: string,
    handleSuccess: (photoList: ITable[]) => void,
    handleError: (errorMessage: string) => void
  ) {
    const userInfos = cookies.get('user');
    const requestOptions = {
      method: 'Get',
      headers: {
        'Content-Type': 'application/json',
        Authorization: 'Bearer ' + userInfos?.token
      }
    };
    const successFunction = (photoList: string) => {
      handleSuccess(JSON.parse(photoList).photoList);
    };
    const errorFunction = (errorMessage: string) => {
      handleError(JSON.parse(errorMessage).message);
    };
    return Server.request(
      `/photo/folder/${folderId}`,
      requestOptions,
      successFunction,
      errorFunction
    );
  }

  static downloadImage(
    id: number,
    handleSuccess: (photo: IPhoto) => void,
    handleError: (errorMessage: IMessage) => void
  ) {
    const URL = `/photo/download`;
    const userInfos = cookies.get('user');
    const requestOptions = {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        Authorization: 'Bearer ' + userInfos?.token
      },
      body: JSON.stringify({
        id
      })
    };
    const successFunction = (photo: string) => {
      handleSuccess(JSON.parse(photo));
    };
    const failFunction = (errorMessage: string) => {
      handleError(JSON.parse(errorMessage));
    };
    return Server.request(URL, requestOptions, successFunction, failFunction);
  }

  static deleteImage(
    ids: number[],
    handleSuccess: (message: IMessage) => void,
    handleError: (errorMessage: IMessage) => void
  ) {
    const URL = `/photo/delete`,
      userInfos = cookies.get('user'),
      requestOptions = {
        method: 'DELETE',
        headers: {
          'Content-Type': 'application/json',
          Authorization: 'Bearer ' + userInfos?.token
        },
        body: JSON.stringify({
          ids
        })
      };
    const successFunction = (message: string) => {
      handleSuccess(JSON.parse(message));
    };
    const errorFunction = (errorMessage: string) => {
      handleError(JSON.parse(errorMessage));
    };
    return Server.request(URL, requestOptions, successFunction, errorFunction);
  }
}
