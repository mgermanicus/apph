import AuthService from '../services/AuthService';

export const getTokenHeader = () => {
  const token = AuthService.getToken();
  if (token) {
    return {
      'Content-Type': 'application/json',
      Authorization: token
    };
  }
};
