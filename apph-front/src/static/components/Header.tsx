import {
  AppBar,
  Box,
  Drawer,
  IconButton,
  List,
  ToggleButtonGroup,
  Toolbar,
  Typography
} from '@mui/material';
import { UserAvatar } from './UserAvatar';
import MenuIcon from '@mui/icons-material/Menu';
import LogoutIcon from '@mui/icons-material/Logout';
import { Link } from 'react-router-dom';
import { IUser } from '../../utils';
import { useState } from 'react';
import { DrawerMenuItem } from './DrawerMenuItem';
import {
  AutoFixHigh,
  Flight,
  Folder,
  Group,
  InsertPhoto,
  LocalOffer,
  Search
} from '@mui/icons-material';
import { useSelector } from 'react-redux';
import AuthService from '../../services/AuthService';
import { useTranslation } from 'react-i18next';
import { setCookieLanguage } from '../../utils/setCookieLanguage';
import MuiToggleButton from '@mui/material/ToggleButton';
import { styled } from '@mui/material/styles';

const appBarStyles = {
  iconButton: {
    mr: 2
  }
};

const ToggleButton = styled(MuiToggleButton)({
  '&.Mui-selected, &.Mui-selected:hover': {
    color: 'white',
    backgroundColor: '#455a64'
  }
});

export const Header = (): JSX.Element => {
  const user = useSelector(
    ({ currentUser }: { currentUser: IUser }) => currentUser
  );
  const [drawerMenuVisible, setDrawerMenuVisible] = useState<boolean>(false);
  const handleLogout = () => {
    AuthService.logout();
  };
  const { t, i18n } = useTranslation();
  const handleChange = (
    event: React.MouseEvent<HTMLElement>,
    newLanguage: string
  ) => {
    i18n.changeLanguage(newLanguage);
    setCookieLanguage(newLanguage);
  };
  return (
    <Box sx={{ flexGrow: 1 }}>
      <AppBar position="relative">
        <Toolbar>
          <IconButton
            size="large"
            edge="start"
            color="inherit"
            aria-label="menu"
            sx={appBarStyles.iconButton}
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
                  title={t('field.photos')}
                  url="/pictures"
                  icon={<InsertPhoto />}
                />
                <DrawerMenuItem
                  title={t('field.folders')}
                  url="/folders"
                  icon={<Folder />}
                />
                <DrawerMenuItem
                  title={t('field.trips')}
                  url="/trips"
                  icon={<Flight />}
                />
                <DrawerMenuItem
                  title={t('field.tags')}
                  url="/tags"
                  icon={<LocalOffer />}
                />
                <DrawerMenuItem
                  title={t('field.treatments')}
                  url="/treatments"
                  icon={<AutoFixHigh />}
                />
                <DrawerMenuItem
                  title={t('field.advancedSearch')}
                  url="/research"
                  icon={<Search />}
                />
                {user.isAdmin && (
                  <DrawerMenuItem
                    title={t('field.users')}
                    url="/users"
                    icon={<Group />}
                  />
                )}
              </List>
            </Box>
          </Drawer>
          <Typography variant="h6" component="div" sx={{ flexGrow: 1 }}>
            APPH
          </Typography>
          <ToggleButtonGroup
            color="standard"
            value={i18n.language}
            exclusive
            onChange={handleChange}
            sx={{ background: '#e3f2fd' }}
          >
            <ToggleButton value="fr">fr</ToggleButton>
            <ToggleButton value="en">en</ToggleButton>
          </ToggleButtonGroup>
          <IconButton component={Link} to="/me">
            <UserAvatar
              firstname={user.firstname || user.login}
              lastname={user.lastname || ''}
            />
          </IconButton>
          <IconButton onClick={handleLogout}>
            <LogoutIcon />
          </IconButton>
        </Toolbar>
      </AppBar>
    </Box>
  );
};
