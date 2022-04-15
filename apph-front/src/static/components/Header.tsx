import { AppBar, Box, IconButton, Toolbar, Typography } from '@mui/material';
import { UserAvatar } from './UserAvatar';
import { makeStyles } from '@mui/styles';
import LoginIcon from '@mui/icons-material/Login';
import MenuIcon from '@mui/icons-material/Menu';
import { Link } from 'react-router-dom';

const makeAppBarStyles = makeStyles({
  appBarStyle: {
    position: 'absolute'
  },
  iconButton: {
    mr: 2
  }
});

export const Header = ({ isAuth }: { isAuth: boolean }): JSX.Element => {
  const classes = makeAppBarStyles();
  return (
    <Box sx={{ flexGrow: 1 }}>
      <AppBar className={classes.appBarStyle}>
        <Toolbar>
          <IconButton
            size="large"
            edge="start"
            color="inherit"
            aria-label="menu"
            className={classes.iconButton}
          >
            <MenuIcon />
          </IconButton>
          <Typography variant="h6" component="div" sx={{ flexGrow: 1 }}>
            APPH
          </Typography>
          {isAuth ? (
            <IconButton component={Link} to="/me">
              <UserAvatar firstname={'Min'} lastname={'SUN'} />
            </IconButton>
          ) : (
            <IconButton color="secondary">
              <LoginIcon />
            </IconButton>
          )}
        </Toolbar>
      </AppBar>
    </Box>
  );
};
