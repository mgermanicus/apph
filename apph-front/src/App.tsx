import React from 'react';
import { Box, createTheme, ThemeProvider } from '@mui/material';
import { UserProfileContainer } from './static/containers/UserProfileContainer';
import { makeStyles } from '@mui/styles';
import { HeaderContainer } from './static/containers/HeaderContainer';

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
      <Box className={classes.textAlignCenter}>
        <HeaderContainer />
        <UserProfileContainer />
      </Box>
    </ThemeProvider>
  );
};
