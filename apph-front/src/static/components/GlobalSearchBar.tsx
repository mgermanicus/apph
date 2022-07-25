import SearchIcon from '@mui/icons-material/Search';
import { alpha, styled } from '@mui/material/styles';
import { useTranslation } from 'react-i18next';
import * as React from 'react';
import { useEffect, useState } from 'react';
import { createSearchParams, useLocation, useNavigate } from 'react-router-dom';
import { useLocation, useNavigate } from 'react-router-dom';
import { AlertColor, Autocomplete, Link, TextField } from '@mui/material';
import PhotoService from '../../services/PhotoService';
import { IMessage } from '../../utils';
import { AlertSnackbar } from './AlertSnackbar';

const SearchIconWrapper = styled('div')(({ theme }) => ({
  margin: theme.spacing(0, 0, 0, 4),
  height: '100%',
  position: 'absolute',
  display: 'flex',
  alignItems: 'center',
  justifyContent: 'center'
}));

const SearchBar = styled('div')(({ theme }) => ({
  position: 'relative',
  borderRadius: theme.shape.borderRadius,
  backgroundColor: alpha(theme.palette.common.white, 0.15),
  '&:hover': {
    backgroundColor: alpha(theme.palette.common.white, 0.25)
  },
  marginLeft: 0,
  marginRight: 5,
  width: '100%',
  [theme.breakpoints.up('sm')]: {
    marginLeft: theme.spacing(1),
    width: 'auto'
  }
}));

export const GlobalSearchBar = (): JSX.Element => {
  const navigate = useNavigate();
  const { t } = useTranslation();
  const location = useLocation();
  const [options, setOptions] = useState<{ title: string }[]>([]);
  const [inputValue, setInputValue] = useState('');
  const [loading, setLoading] = React.useState<boolean>(false);
  const [message, setMessage] = useState('');
  const [snackbarOpen, setSnackbarOpen] = useState<boolean>(false);
  const [severity, setSeverity] = useState<AlertColor>();

  const traitError = (error: IMessage) => {
    setMessage(error.message);
    setSnackbarOpen(true);
    setSeverity('error');
  };

  const handleSubmit = () => {
    navigate({
      pathname: '/search/global/',
      search: `?${createSearchParams({
        params: inputValue
      })}`
    });
  };

  const fuzzyWord = () => {
    if (inputValue && inputValue?.length >= 2) {
      setLoading(true);
      PhotoService.searchFuzzy(
        inputValue,
        (photoList) => {
          const matches: Set<string> = new Set();
          const regex = new RegExp(`${inputValue}`, 'gi');
          photoList.forEach((photo) => {
            photo.title
              .toLowerCase()
              .replace(/[^a-z\s\-_]/gi, '')
              .split(' ')
              .filter((s) => s.match(regex))
              .forEach((s) => matches.add(s));
            photo.location.address
              .toLowerCase()
              .replace(/[^a-z\s\-_]/gi, '')
              .split(' ')
              .filter((s) => s.match(regex))
              .forEach((s) => matches.add(s));
            photo.description
              .toLowerCase()
              .replace(/[^a-z\s\-_]/gi, '')
              .split(' ')
              .filter((s) => s.match(regex))
              .forEach((s) => matches.add(s));
            photo.tags.forEach((tag) =>
              tag.name
                .toLowerCase()
                .replace(/[^a-z\s\-_]/gi, '')
                .split(' ')
                .filter((s) => s.match(regex))
                .forEach((s) => matches.add(s))
            );
          });
          const titleMatches: { title: string }[] = [];
          matches.forEach((s) => titleMatches.push({ title: s }));
          setOptions(titleMatches);
        },
        (error: IMessage) => traitError(error)
      ).finally(() => setLoading(false));
    } else {
      setOptions([]);
    }
  };

  useEffect(() => {
    fuzzyWord();
  }, [inputValue]);

  useEffect(() => {
    setInputValue('');
  }, [location]);

  return (
    <>
      <SearchIconWrapper
        aria-label="search"
        onClick={handleSubmit}
        title={t('field.Search')}
      >
        <SearchIcon />
      </SearchIconWrapper>
      <SearchBar>
        <Autocomplete
          freeSolo
          onKeyPress={(event) => {
            if (event.key === 'Enter') {
              handleSubmit();
            }
          }}
          inputValue={inputValue}
          onInputChange={(_event, newInputValue) => {
            setInputValue(newInputValue);
          }}
          id="controllable-states-demo"
          options={options}
          loading={loading}
          sx={(theme) => ({
            minWidth: '15ch',
            '.MuiInputLabel-root': {
              color: '#ffffff',
              '.Mui-focused': {
                color: '#ffffff'
              }
            },
            '& .MuiInputBase-input': {
              color: '#ffffff',
              padding: theme.spacing(1, 1, 1, 0),
              // vertical padding + font size from searchIcon
              paddingLeft: `calc(1em + ${theme.spacing(4)})`,
              transition: theme.transitions.create('width'),
              width: '15ch',
              [theme.breakpoints.up('sm')]: {
                width: '12ch',
                '&:focus': {
                  width: '20ch'
                }
              }
            }
          })}
          renderInput={(params) => (
            <TextField {...params} label={t('field.Search') + '...'} />
          )}
          getOptionLabel={(option) =>
            option.title ? option.title : inputValue
          }
          renderOption={(props, option) => (
            <li {...props}>
              <Link
                component="button"
                variant="body2"
                onClick={() => {
                  window.location.href = `/search/global/${option.title}`;
                }}
              >
                {option.title}
              </Link>
            </li>
          )}
        />
        <AlertSnackbar
          open={snackbarOpen}
          severity={severity}
          message={t(message)}
          onClose={setSnackbarOpen}
        />
      </SearchBar>
    </>
  );
};
