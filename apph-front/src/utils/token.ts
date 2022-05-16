import AuthService from '../services/AuthService';

export const getTokenHeader = (contentType = 'application/json') => {
  const token = AuthService.getToken();
  if (token) {
    return {
      'Content-Type': contentType,
      Authorization: 'Bearer ' + token
    };
  }
};
