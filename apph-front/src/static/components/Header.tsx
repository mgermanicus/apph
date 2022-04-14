import { AppBar, Box, IconButton, Toolbar, Typography } from '@mui/material';
import MenuIcon from '@mui/icons-material/Menu';
import { UserAvatar } from './UserAvatar';

export const Header = ({ isAuth }: { isAuth: boolean }): JSX.Element => {
  return (
    <Box sx={{ flexGrow: 1 }}>
      <AppBar position="static">
        <Toolbar>
          <IconButton
            size="large"
            edge="start"
            color="inherit"
            aria-label="menu"
            sx={{ mr: 2 }}
          >
            <MenuIcon />
          </IconButton>
          <Typography variant="h6" component="div" sx={{ flexGrow: 1 }}>
            APPH
          </Typography>
          {isAuth ? <UserAvatar firstname={'Min'} lastname={'SUN'} /> : 'LOGIN'}
        </Toolbar>
      </AppBar>
    </Box>
  );
};
