import Cookies from 'universal-cookie';

export const setCookieLanguage = (language: string) => {
  const cookies = new Cookies();
  cookies.set(
    'userPreferences',
    { language: language },
    { expires: new Date(Date.now() + 10 ** 10) }
  );
};
