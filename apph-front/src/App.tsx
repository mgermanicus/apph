import React from 'react';
import { Box, createTheme, ThemeProvider } from '@mui/material';
import { makeStyles } from '@mui/styles';
import { PrivateRoutes } from './privateRoutes';
import { BrowserRouter } from 'react-router-dom';
import { Header } from './static/components/Header';

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
  return (
    <ThemeProvider theme={theme}>
      <BrowserRouter>
        <Box className={classes.textAlignCenter}>
          <Header isAuth={true} />
          <PrivateRoutes />
        </Box>
      </BrowserRouter>
    </ThemeProvider>
  );
};
