export const authHeader = () => {
  const tokenFromLocalStorage = localStorage.getItem('token');
  if (tokenFromLocalStorage) {
    return {
      'Content-Type': 'application/json',
      Authentication: tokenFromLocalStorage
    };
  }
};
