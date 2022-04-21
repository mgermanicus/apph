const API_URL = 'http://localhost:8080/';

export default class ImageService {
  static async handleResponse(response: Response) {
    const body = await response.json();
    if (!response.ok) throw Error("Une erreur est survenue lors de l'upload");
    return body;
  }

  static async uploadImage(title: string, imageFile: File) {
    const formData = new FormData();
    formData.append('file', imageFile);
    formData.append('name', title);
    const requestOptions = {
      method: 'POST',
      body: formData
    };
    const response = await fetch(`${API_URL}photo/upload`, requestOptions);
    return this.handleResponse(response);
  }
}
