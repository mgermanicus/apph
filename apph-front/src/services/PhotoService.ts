import Server from './Server';
import { imageFileCheck, IMessage, IPagination, IPhoto, ITag } from '../utils';
import Cookies from 'universal-cookie';

const cookies = new Cookies();
export default class PhotoService {
  static uploadImage(
    title: string,
    description: string,
    shootingDate: Date,
    imageFile: File,
    selectedTags: ITag[],
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
    const requestOptions = {
      method: 'POST',
      headers: {
        Authorization: 'Bearer ' + userInfos?.token
      },
      body: formData
    };
    return Server.request(
      `/photo/upload`,
      requestOptions,
      handleSuccess,
      handleError
    );
  }

  static getData(
    pageSize: number,
    page: number,
    handleSuccess: (pagination: IPagination) => void,
    handleError: (errorMessage: string) => void
  ) {
    const URL = `/photo/infos?pageSize=${encodeURIComponent(
      pageSize
    )}&page=${encodeURIComponent(page)}`;
    const userInfos = cookies.get('user');
    const requestOptions = {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
        Authorization: 'Bearer ' + userInfos?.token
      }
    };
    const successFunction = (val: string) => {
      handleSuccess(JSON.parse(val));
    };
    const errorFunction = (errorMessage: string) => {
      handleError(JSON.parse(errorMessage).message);
    };
    return Server.request(URL, requestOptions, successFunction, errorFunction);
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
          Authorization: userInfos?.token
        },
        body: JSON.stringify({
          ids
        })
      };
    const successFunction = (message: string) => {
      handleSuccess(JSON.parse(message));
    };
    const failFunction = (errorMessage: string) => {
      handleError(JSON.parse(errorMessage));
    };
    return Server.request(URL, requestOptions, successFunction, failFunction);
  }
}
