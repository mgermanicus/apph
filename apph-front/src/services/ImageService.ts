import Server from './Server';
import { imageFileCheck } from '../utils';

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
}
