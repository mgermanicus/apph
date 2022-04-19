import cryptoJS from 'crypto-js';

const API_URL = process.env['REACT_APP_API_URL'];

class UserService {
  private static handleResponse(res: Response) {
    if (!res.ok) {
      throw new Error(`HTTP status code: ${res.status}`);
    }
    return res.json();
  }

  static async signIn(email: string, password: string) {
    const requestOptions = {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        email,
        password: cryptoJS.SHA256(password).toString()
      })
    };
    try {
      return await fetch(`${API_URL}/auth/signIn`, requestOptions).then((res) =>
        this.handleResponse(res)
      );
    } catch (e) {
      console.log('Erreur lors de la connexion au serveur');
      return null;
    }
  }
}

export default UserService;
