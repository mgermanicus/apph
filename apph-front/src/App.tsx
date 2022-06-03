import React, { useEffect } from 'react';
import { Box, createTheme, ThemeProvider } from '@mui/material';
import { PrivateRoutes } from './privateRoutes';
import { BrowserRouter } from 'react-router-dom';
import AuthService from './services/AuthService';
import { changeCurrentUser } from './redux/slices/userSlice';
import { useDispatch } from 'react-redux';
import { enUS, frFR } from '@mui/material/locale';
import { useTranslation } from 'react-i18next';
import Cookies from 'universal-cookie';
const cookies = new Cookies();

const appStyles = {
  textAlignCenter: {
    textAlign: 'center'
  }
};

export const App = () => {
  const { t, i18n } = useTranslation();
  const theme = createTheme(
    {
      palette: {
        background: {
          paper: '#f2f2f2'
        }
      }
    },
    i18n.language == 'fr' ? frFR : enUS
  );
  const dispatch = useDispatch();
  const authenticated = AuthService.isTokenValid();
  useEffect(() => {
    const languagePreference = cookies.get('userPreferences');
    if (cookies.get('userPreferences'))
      i18n.changeLanguage(languagePreference.language);
    try {
      if (AuthService.isTokenValid()) {
        dispatch(changeCurrentUser(AuthService.getUserLoginByToken()));
      }
    } catch (e) {
      alert(t('user.error.expiredSession'));
    }
  }, []);
  return (
    <ThemeProvider theme={theme}>
      <BrowserRouter>
        <Box sx={appStyles.textAlignCenter}>
          <PrivateRoutes authenticated={authenticated} />
        </Box>
      </BrowserRouter>
    </ThemeProvider>
  );
};
