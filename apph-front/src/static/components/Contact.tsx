import {
  AlertColor,
  Grid,
  IconButton,
  InputAdornment,
  Tooltip
} from '@mui/material';
import * as React from 'react';
import TextField from '@mui/material/TextField';
import { useTranslation } from 'react-i18next';
import { Add } from '@mui/icons-material';
import { AlertSnackbar } from './AlertSnackbar';
import { useEffect, useState } from 'react';
import { IUser } from '../../utils';
import UserService from '../../services/UserService';
import { ContactCard } from './ContactCard';

export const Contact = (): JSX.Element => {
  const [contactList, setContactList] = useState<IUser[]>([]);
  const [newContact, setNewContact] = useState<string>('');
  const [snackMessage, setSnackMessage] = useState<string>('');
  const [snackSeverity, setSnackSeverity] = useState<AlertColor>('warning');
  const [snackbarOpen, setSnackbarOpen] = useState<boolean>(false);

  const { t } = useTranslation();
  const emailValidator =
    /^(([^<>()[\]\\.,;:\s@"]+(\.[^<>()[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;

  const getContact = () => {
    UserService.getContact(
      (list: IUser[]) => {
        setContactList(list);
      },
      (errorMessage: string) => {
        setSnackSeverity('error');
        setSnackMessage(errorMessage);
        setSnackbarOpen(true);
      }
    );
  };

  useEffect(() => {
    getContact();
  }, []);

  const handleAddContact = () => {
    setSnackbarOpen(false);
    if (newContact.length == 0) {
      setSnackSeverity('warning');
      setSnackMessage('user.contact.error.fillInput');
      setSnackbarOpen(true);
    } else if (!emailValidator.test(newContact)) {
      setSnackSeverity('error');
      setSnackMessage('user.contact.error.email');
      setSnackbarOpen(true);
    } else {
      UserService.addContact(
        newContact,
        (list: IUser[]) => {
          setContactList(list);
        },
        (errorMessage: string) => {
          setSnackSeverity('error');
          setSnackMessage(errorMessage);
          setSnackbarOpen(true);
        }
      );
    }
  };

  return (
    <Grid container sx={{ display: 'flex', flexDirection: 'column' }}>
      <Grid container>
        <Grid item xs={6} sx={{ textAlign: 'start', marginBottom: '20px' }}>
          <TextField
            id="email"
            label={t('user.contact.new')}
            name="email"
            autoComplete="email"
            type="email"
            size="small"
            value={newContact}
            onChange={(event) => setNewContact(event.currentTarget.value)}
            onKeyDown={async (event) => {
              if (event.key === 'Enter') {
                event.preventDefault();
                handleAddContact();
              }
            }}
            InputProps={{
              endAdornment: (
                <InputAdornment position="end">
                  <Tooltip title={t('user.contact.add')}>
                    <IconButton
                      edge="end"
                      color="primary"
                      onClick={handleAddContact}
                      aria-label="add-contact"
                    >
                      <Add />
                    </IconButton>
                  </Tooltip>
                </InputAdornment>
              )
            }}
          />
        </Grid>
      </Grid>
      <Grid
        container
        spacing={{ xs: 2, md: 3 }}
        columns={{ xs: 8, sm: 12, md: 16 }}
        sx={{ px: 5 }}
      >
        {contactList.map((user) => (
          <Grid item xs={4} sm={6} md={8} key={'key' + user.login}>
            <ContactCard
              firstname={user.firstname}
              lastname={user.lastname}
              login={user.login}
            />
          </Grid>
        ))}
      </Grid>
      <AlertSnackbar
        open={snackbarOpen}
        severity={snackSeverity}
        message={t(snackMessage)}
        onClose={setSnackbarOpen}
      />
    </Grid>
  );
};
