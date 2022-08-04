import { UserProfile } from '../components/UserProfile';
import { useEffect, useState } from 'react';
import { IUser } from '../../utils';
import { ErrorCard } from '../components/ErrorCard';
import UserService from '../../services/UserService';
import { useTranslation } from 'react-i18next';
import { Box, Tab, Tabs } from '@mui/material';
import { TabPanel } from '../components/TabPanel';
import { Contact } from '../components/Contact';

export const UserProfilePage = (): JSX.Element => {
  const [firstname, setFirstname] = useState<string>('');
  const [lastname, setLastname] = useState<string>('');
  const [email, setEmail] = useState<string>('');
  const [error, setError] = useState<string>();
  const [tabIndex, setTabIndex] = useState<number>(0);
  const { t } = useTranslation();

  useEffect(() => {
    (async () => {
      await UserService.getUser(
        (user: string) => {
          const userConverted: IUser = JSON.parse(user);
          setFirstname(userConverted.firstname);
          setLastname(userConverted.lastname);
          setEmail(userConverted.login);
        },
        (errorMessage: string) => {
          setError(errorMessage);
        }
      );
    })();
  }, []);

  return (
    <>
      {error ? (
        <ErrorCard errorMessage={t(error)} />
      ) : (
        <Box sx={{ flexGrow: 1, display: 'flex' }}>
          <Tabs
            orientation="vertical"
            value={tabIndex}
            onChange={(_event, value) => setTabIndex(value)}
            sx={{
              borderRight: 1,
              borderColor: 'divider',
              width: '20%',
              height: '30%'
            }}
          >
            <Tab label="Profil" />
            <Tab label="Contact" />
          </Tabs>
          <div style={{ width: '80%' }}>
            <TabPanel index={0} value={tabIndex}>
              <UserProfile
                firstname={firstname}
                lastname={lastname}
                login={email}
              />
            </TabPanel>
            <TabPanel index={1} value={tabIndex}>
              <Contact />
            </TabPanel>
          </div>
        </Box>
      )}
    </>
  );
};
