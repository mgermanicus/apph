const API_URL = 'http://localhost:8080/';

export default class ImageService {
  static uploadImage(title: string, imageFile: File) {
    const formData = new FormData();
    formData.append('file', imageFile);
    formData.append('name', title);
    const requestOptions = {
      method: 'POST',
      body: formData
    };

    try {
      return fetch(`${API_URL}photo/upload`, requestOptions).then((res) =>
        //this.handleResponse(res)
        // TODO
        console.log(res)
      );
    } catch (error) {
      //return this.handleConnectionError(error);
      // TODO
    }
  }
}
