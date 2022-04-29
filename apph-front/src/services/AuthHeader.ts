import AuthService from './AuthService';

export const authHeader = () => {
  const user = AuthService.getCurrentUser();
  if (user.token) {
    return {
      'Content-Type': 'application/json',
      Authentication: user.token
    };
  }
};
