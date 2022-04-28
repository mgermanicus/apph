import {
  AppBar,
  Box,
  Drawer,
  IconButton,
  List,
  Toolbar,
  Typography
} from '@mui/material';
import { UserAvatar } from './UserAvatar';
import LoginIcon from '@mui/icons-material/Login';
import MenuIcon from '@mui/icons-material/Menu';
import { Link } from 'react-router-dom';
import { IUser, makeAppBarStyles } from '../../utils';
import { useState } from 'react';
import { DrawerMenuItem } from './DrawerMenuItem';
import {
  AutoFixHigh,
  Flight,
  Folder,
  InsertPhoto,
  LocalOffer,
  Search
} from '@mui/icons-material';
import { useSelector } from 'react-redux';

export const Header = ({ isAuth }: { isAuth: boolean }): JSX.Element => {
  const classes = makeAppBarStyles();
  const user = useSelector(
    ({ currentUser }: { currentUser: IUser }) => currentUser
  );

  const [drawerMenuVisible, setDrawerMenuVisible] = useState<boolean>(false);
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
            onClick={() => setDrawerMenuVisible(true)}
          >
            <MenuIcon />
          </IconButton>
          <Drawer
            open={drawerMenuVisible}
            onClose={() => setDrawerMenuVisible(false)}
          >
            <Box
              onClick={() => setDrawerMenuVisible(false)}
              onKeyDown={() => setDrawerMenuVisible(false)}
            >
              <List>
                <DrawerMenuItem
                  title="Mes Photos"
                  url="pictures"
                  icon={<InsertPhoto />}
                />
                <DrawerMenuItem
                  title="Mes Dossiers"
                  url="/folders"
                  icon={<Folder />}
                />
                <DrawerMenuItem
                  title="Mes Voyages"
                  url="/trips"
                  icon={<Flight />}
                />
                <DrawerMenuItem
                  title="Mes Tags"
                  url="/tags"
                  icon={<LocalOffer />}
                />
                <DrawerMenuItem
                  title="Mes Traitements"
                  url="/treatments"
                  icon={<AutoFixHigh />}
                />
                <DrawerMenuItem
                  title="Recherche Avancée"
                  url="/research"
                  icon={<Search />}
                />
              </List>
            </Box>
          </Drawer>
          <Typography variant="h6" component="div" sx={{ flexGrow: 1 }}>
            APPH
          </Typography>
          {isAuth ? (
            <IconButton component={Link} to="/me">
              <UserAvatar
                firstname={user.firstname ?? user.login}
                lastname={user.lastname ?? ''}
              />
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
