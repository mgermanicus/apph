import React, { useEffect } from 'react';
import { Box, createTheme, ThemeProvider } from '@mui/material';
import { makeStyles } from '@mui/styles';
import { PrivateRoutes } from './privateRoutes';
import { BrowserRouter } from 'react-router-dom';
import AuthService from './services/AuthService';
import { changeCurrentUser } from './redux/slices/userSlice';
import { useDispatch } from 'react-redux';

const useStyles = makeStyles({
  textAlignCenter: {
    textAlign: 'center'
  }
});

const theme = createTheme({
  palette: {
    background: {
      paper: '#f2f2f2'
    }
  }
});

export const App = () => {
  const classes = useStyles();
  const dispatch = useDispatch();

  useEffect(() => {
    try {
      if (AuthService.isTokenValid()) {
        dispatch(changeCurrentUser(AuthService.getUserLoginByToken()));
      }
    } catch (e) {
      console.error(e);
    }
  }, []);

  return (
    <ThemeProvider theme={theme}>
      <BrowserRouter>
        <Box className={classes.textAlignCenter}>
          <PrivateRoutes />
        </Box>
      </BrowserRouter>
    </ThemeProvider>
  );
};
