import { IFolder } from '../utils/types/Folder';

//TODO replace by the .env variable
const API_URL = 'http://localhost:8080/folder/';

export class FolderService {
  static async getFolders(id: number): Promise<IFolder | undefined> {
    const resquestOptions = {
      method: 'Get',
      headers: {
        'Content-Type': 'application/json'
      }
    };
    try {
      return await fetch(`${API_URL}${id}`, resquestOptions).then((res) =>
        this.handleResponse(res)
      );
    } catch (e) {
      console.log('Erreur lors de la connexion au serveur');
    }
  }

  private static async handleResponse(res: Response): Promise<IFolder> {
    if (!res.ok) {
      throw new Error(`HTTP status code: ${res.status}`);
    }
    return res.json();
  }
}
