export const authHeader = () => {
  const userFromLocalStorage = localStorage.getItem('user');
  if (userFromLocalStorage) {
    const user = JSON.parse(userFromLocalStorage);
    return user.accessToken
      ? {
          'Content-Type': 'application/json',
          Authorization: user.accessToken
        }
      : {
          'Content-Type': 'application/json',
          Authorization: undefined
        };
  }
};
