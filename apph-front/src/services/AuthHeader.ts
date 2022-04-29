import AuthService from './AuthService';

export const authHeader = () => {
  const token = AuthService.getToken();
  if (token) {
    return {
      'Content-Type': 'application/json',
      Authentication: token
    };
  }
};
