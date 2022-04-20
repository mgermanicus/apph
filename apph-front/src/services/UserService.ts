import cryptoJS from 'crypto-js';
import jwtDecode from 'jwt-decode';
import Cookies from 'universal-cookie';

const API_URL = process.env['REACT_APP_API_URL'];
const cookies = new Cookies();

class UserService {
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
      return await fetch(`${API_URL}/auth/signIn`, requestOptions)
        .then(async (response) => {
          if (response.ok) {
            return response.text();
          }
          throw new Error(await response.text());
        })
        .then((jws) => {
          const decodedToken = jwtDecode(jws);
          if (decodedToken !== null && typeof decodedToken === 'object') {
            cookies.set('user', { ...decodedToken, token: jws });
          }
        });
    } catch (e) {
      await Promise.reject(e);
    }
  }
}

export default UserService;
