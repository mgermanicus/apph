import Server from './Server';
import { imageFileCheck } from '../utils';
import { IPhoto } from '../utils/types/Photo';

export default class ImageService {
  static uploadImage(
    title: string,
    imageFile: File,
    handleSuccess: () => void,
    handleError: (errorMessage: string) => void
  ) {
    if (!imageFileCheck(imageFile, handleError)) return;
    const formData = new FormData();
    formData.append('file', imageFile);
    formData.append('name', title);
    const requestOptions = {
      method: 'POST',
      body: formData
    };
    return Server.request(
      `/photo/upload`,
      requestOptions,
      handleSuccess,
      handleError
    );
  }

  static downloadImage(
    id: number,
    handleSuccess: (photo: IPhoto) => void,
    handleError: (errorMessage: string) => void
  ) {
    const URL = `/photo/download`;
    const requestOptions = {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        id
      })
    };
    const successFunction = (photo: string) => {
      handleSuccess(JSON.parse(photo));
    };
    return Server.request(URL, requestOptions, successFunction, handleError);
  }
}
