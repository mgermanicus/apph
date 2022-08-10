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
import { GridSortItem } from '@mui/x-data-grid';
import { ILocation, IMarker } from '../utils/types/Location';

const cookies = new Cookies();
export default class PhotoService {
  static uploadImage(
    title: string,
    description: string,
    shootingDate: string,
    imageFile: File,
    location: ILocation,
    selectedTags: ITag[],
    maxFileSizeMb: number,
    handleSuccess: () => void,
    handleError: (errorMessage: string) => void
  ) {
    if (!imageFileCheck(imageFile, handleError, maxFileSizeMb)) return;
    const userInfos = cookies.get('user');
    const formData = new FormData();
    formData.append('file', imageFile);
    formData.append('location', JSON.stringify(location));
    formData.append('tags', JSON.stringify(selectedTags));
    formData.append('title', title);
    formData.append('description', description);
    formData.append('shootingDate', shootingDate);
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
    sortModel?: GridSortItem,
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
        sortModel,
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

  static editInfos(
    id: number,
    title: string,
    description: string,
    location: ILocation,
    tags: ITag[],
    shootingDate: string,
    handleSuccess: (message: string) => void,
    handleError: (errorMessage: string) => void
  ) {
    const URL = '/photo/editInfos';
    const userInfos = cookies.get('user');
    const formData = new FormData();
    formData.append('id', id.toString());
    formData.append('title', title);
    formData.append('description', description);
    formData.append('location', JSON.stringify(location));
    formData.append('tags', JSON.stringify(tags));
    formData.append('shootingDate', shootingDate);
    const requestOptions = {
      method: 'POST',
      headers: {
        Authorization: 'Bearer ' + userInfos?.token
      },
      body: formData
    };
    const successFunction = (val: string) => {
      handleSuccess(JSON.parse(val).message);
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

  static movePhotos(
    ids: number[],
    folderId: string,
    handleSuccess: (message: string[]) => void,
    handleError: (errorMessage: IMessage) => void
  ) {
    const URL = `/photo/folder/move`,
      userInfos = cookies.get('user'),
      requestOptions = {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          Authorization: 'Bearer ' + userInfos?.token
        },
        body: JSON.stringify({
          ids,
          folderId
        })
      };
    const successFunction = (message: string) => {
      handleSuccess(JSON.parse(message).messageList);
    };
    const errorFunction = (errorMessage: string) => {
      handleError(JSON.parse(errorMessage));
    };
    return Server.request(URL, requestOptions, successFunction, errorFunction);
  }

  static downloadZip(
    ids: number[],
    handleSuccess: (photos: IPhoto) => void,
    handleError: (errorMessage: IMessage) => void
  ) {
    const URL = `/photo/download/zip`,
      userInfos = cookies.get('user'),
      requestOptions = {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          Authorization: 'Bearer ' + userInfos?.token
        },
        body: JSON.stringify({
          ids
        })
      };
    const successFunction = (photos: string) => {
      handleSuccess(JSON.parse(photos));
    };
    const errorFunction = (errorMessage: string) => {
      handleError(JSON.parse(errorMessage));
    };
    return Server.request(URL, requestOptions, successFunction, errorFunction);
  }

  static reUploadImage(
    id: number,
    imageFile: File,
    maxFileSizeMb: number,
    handleSuccess: () => void,
    handleError: (errorMessage: string) => void
  ) {
    if (!imageFileCheck(imageFile, handleError, maxFileSizeMb)) return;
    const userInfos = cookies.get('user');
    const formData = new FormData();
    formData.append('id', id.toString());
    formData.append('file', imageFile);
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
      `/photo/reupload`,
      requestOptions,
      handleSuccess,
      errorFunction
    );
  }

  static updatePhotoInfo(
    id: number,
    title: string,
    description: string,
    folderId: number | null,
    handleSuccess: (message: { statusCode: number; message: string }) => void,
    handleError: (errorMessage: IMessage) => void
  ) {
    const URL = `/photo/update`,
      userInfos = cookies.get('user'),
      requestOptions = {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          Authorization: 'Bearer ' + userInfos?.token
        },
        body: JSON.stringify({
          id,
          title,
          description,
          folderId
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

  static getPhotoUrls(
    ids: number[],
    handleSuccess: (photoList: ITable[]) => void,
    handleError: () => void
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

    return Server.request(
      `/photo/${ids}`,
      requestOptions,
      successFunction,
      handleError
    );
  }

  static editPhotoListInfos(
    ids: number[],
    handleSuccess: (successMessage: string) => void,
    handleError: (errorMessage: string) => void,
    shootingDate?: string,
    selectedTags?: ITag[],
    location?: ILocation
  ) {
    const URL = `/photo/editPhotoList`,
      userInfos = cookies.get('user'),
      requestOptions = {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          Authorization: 'Bearer ' + userInfos?.token
        },
        body: JSON.stringify({
          ids,
          shootingDate: shootingDate,
          tags: JSON.stringify(selectedTags),
          location: JSON.stringify(location)
        })
      };
    const successFunction = (message: string) => {
      handleSuccess(JSON.parse(message).message);
    };
    const errorFunction = (errorMessage: string) => {
      handleError(JSON.parse(errorMessage).message);
    };
    return Server.request(URL, requestOptions, successFunction, errorFunction);
  }

  static search(
    target: string | null,
    page: number,
    pageSize: number,
    handleSuccess: (
      photoList: ITable[],
      total: number,
      tagFacets: Record<string, Record<never, number>>
    ) => void,
    handleError: (errorMessage: IMessage) => void
  ) {
    const userInfos = cookies.get('user');
    const requestOptions = {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        Authorization: 'Bearer ' + userInfos?.token
      },
      body: JSON.stringify({
        target,
        page,
        pageSize
      })
    };
    const successFunction = (results: string) => {
      handleSuccess(
        JSON.parse(results).photoList,
        JSON.parse(results).total,
        JSON.parse(results).facets
      );
    };
    const errorFunction = (errorMessage: string) => {
      handleError(JSON.parse(errorMessage));
    };
    return Server.request(
      `/photo/search`,
      requestOptions,
      successFunction,
      errorFunction
    );
  }

  static sendPhotos(
    recipient: string,
    subject: string,
    content: string,
    ids: number[],
    handleSuccess: (successMessage: string) => void,
    handleError: (errorMessage: string) => void
  ) {
    const URL = `/email/sendAttachment`,
      userInfos = cookies.get('user'),
      requestOptions = {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          Authorization: 'Bearer ' + userInfos?.token
        },
        body: JSON.stringify({
          recipient,
          subject,
          content,
          ids
        })
      };
    const successFunction = (message: string) => {
      handleSuccess(JSON.parse(message).message);
    };
    const errorFunction = (errorMessage: string) => {
      handleError(JSON.parse(errorMessage).message);
    };
    return Server.request(URL, requestOptions, successFunction, errorFunction);
  }

  static getMarkers(
    handleSuccess: (markers: IMarker[]) => void,
    handleError: (errorMessage: string) => void
  ) {
    const userInfos = cookies.get('user');
    const requestOptions = {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
        Authorization: 'Bearer ' + userInfos?.token
      }
    };
    const successFunction = (results: string) => {
      handleSuccess(JSON.parse(results).markerList);
    };
    const errorFunction = (errorMessage: string) => {
      handleError(JSON.parse(errorMessage));
    };
    return Server.request(
      `/photo/markers`,
      requestOptions,
      successFunction,
      errorFunction
    );
  }
}
