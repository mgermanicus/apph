import { StatusCodes } from 'http-status-codes';
const API_URL = 'http://localhost:8080/';
const MAX_FILE_SIZE_MB = 10;

export default class ImageService {
  static handleResponse(response: Response) {
    const body = response.json();
    if (response.ok) return body;
    if (response.status == StatusCodes.REQUEST_TOO_LONG)
      throw Error('La taille du fichier excède la limite maximale');
    else throw Error("Une erreur est survenue lors de l'upload");
  }

  static handleConnectionError(reason: any) {
    if (reason instanceof Error) throw reason;
    throw new Error('Une erreur est survenue lors de la connexion au serveur');
  }

  static uploadImage(title: string, imageFile: File) {
    console.log(imageFile.size);
    if (imageFile.size > MAX_FILE_SIZE_MB * 1000000)
      throw Error(
        `La taille du fichier excède la limite maximale (${MAX_FILE_SIZE_MB} MB)`
      );
    const formData = new FormData();
    formData.append('file', imageFile);
    formData.append('name', title);
    const requestOptions = {
      method: 'POST',
      body: formData
    };
    return fetch(`${API_URL}photo/upload`, requestOptions)
      .then((response) => this.handleResponse(response))
      .catch((reason) => this.handleConnectionError(reason));
  }
}
