const API_URL = 'http://localhost:8080/';

export default class ImageService {
  static handleResponse(response: Response) {
    const body = response.json();
    if (!response.ok) throw Error("Une erreur est survenue lors de l'upload");
    return body;
  }

  static handleConnectionError() {
    throw new Error('Une erreur est survenue lors de la connexion au serveur');
  }

  static uploadImage(title: string, imageFile: File) {
    const formData = new FormData();
    formData.append('file', imageFile);
    formData.append('name', title);
    const requestOptions = {
      method: 'POST',
      body: formData
    };
    return fetch(`${API_URL}photo/upload`, requestOptions)
      .then((response) => this.handleResponse(response))
      .catch((error) => this.handleConnectionError());
  }
}
