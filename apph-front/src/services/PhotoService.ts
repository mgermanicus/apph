import Server from './Server';
import { imageFileCheck } from '../utils';
import Cookies from 'universal-cookie';
import { ITable } from '../utils/types/table';

const cookies = new Cookies();
export default class PhotoService {
  static uploadImage(
    title: string,
    imageFile: File,
    handleSuccess: () => void,
    handleError: (errorMessage: string) => void
  ) {
    if (!imageFileCheck(imageFile, handleError)) return;
    const userInfos = cookies.get('user');
    const formData = new FormData();
    formData.append('file', imageFile);
    formData.append('title', title);
    const requestOptions = {
      method: 'POST',
      headers: {
        Authorization: userInfos?.token
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
    handleSuccess: (tab: Array<ITable>) => void,
    handleError: (errorMessage: string) => void
  ) {
    const URL = `/photo/infos`;
    const userInfos = cookies.get('user');
    const requestOptions = {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
        Authorization: userInfos?.token
      }
    };
    const successFunction = (val: string) => {
      let i = 1;
      const tab: Array<ITable> = JSON.parse(val);
      for (const elt of tab) {
        elt.id = i++;
      }
      handleSuccess(tab);
    };
    const errorFunction = (errorMessage: string) => {
      handleError(errorMessage);
    };
    return Server.request(URL, requestOptions, successFunction, errorFunction);
  }
}
