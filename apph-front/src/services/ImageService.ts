import Server from './Server';
import imageFileCheck from '../utils/imageFileCheck';
const API_URL = 'http://localhost:8080/';
const cookies = new Cookies();
const { token } = cookies.get('user');
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
    const successFunction = (body: string) => {
      handleSuccess();
    };
    const errorFunction = (errorMessage: string) => {
      handleError(errorMessage);
    };
    return Server.request(
      `${API_URL}photo/upload`,
      requestOptions,
      successFunction,
      errorFunction
    );
  }
}
