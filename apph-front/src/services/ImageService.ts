const API_URL = 'http://localhost:8080/';

export default class ImageService {
  static async uploadImage(title: string, imageFile: File) {
    const formData = new FormData();
    formData.append('file', imageFile);
    formData.append('name', title);
    const requestOptions = {
      method: 'POST',
      body: formData
    };
    const response = await fetch(`${API_URL}photo/upload`, requestOptions);
    if (!response.ok) throw Error; // TODO customize error
  }
}
