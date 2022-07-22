import SearchIcon from '@mui/icons-material/Search';
import { alpha, styled } from '@mui/material/styles';
import InputBase from '@mui/material/InputBase';
import { useTranslation } from 'react-i18next';
import * as React from 'react';
import { useEffect, useState } from 'react';
import { createSearchParams, useLocation, useNavigate } from 'react-router-dom';

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

const SearchIconWrapper = styled('div')(({ theme }) => ({
  padding: theme.spacing(0, 2),
  height: '100%',
  position: 'absolute',
  zIndex: 999,
  display: 'flex',
  alignItems: 'center',
  justifyContent: 'center'
}));

const StyledInputBase = styled(InputBase)(({ theme }) => ({
  color: 'inherit',
  '& .MuiInputBase-input': {
    padding: theme.spacing(1, 1, 1, 0),
    // vertical padding + font size from searchIcon
    paddingLeft: `calc(1em + ${theme.spacing(4)})`,
    transition: theme.transitions.create('width'),
    width: '100%',
    [theme.breakpoints.up('sm')]: {
      width: '12ch',
      '&:focus': {
        width: '20ch'
      }
    }
  }
}));

export const GlobalSearchBar = (): JSX.Element => {
  const navigate = useNavigate();
  const { t } = useTranslation();
  const [content, setContent] = useState<string>('');
  const location = useLocation();

  const handleSubmit = () => {
    navigate({
      pathname: '/search/global/',
      search: `?${createSearchParams({
        params: content
      })}`
    });
  };

  useEffect(() => {
    setContent('');
  }, [location]);

  return (
    <SearchBar>
      <SearchIconWrapper
        aria-label="search"
        onClick={handleSubmit}
        title={t('field.Search')}
      >
        <SearchIcon />
      </SearchIconWrapper>
      <StyledInputBase
        placeholder={t('field.Search') + '...'}
        inputProps={{ 'aria-label': 'search', maxLength: 127 }}
        value={content}
        name="content"
        onChange={(event) => {
          setContent(event.target.value);
        }}
        onKeyPress={(event) => {
          if (event.key === 'Enter') {
            handleSubmit();
          }
        }}
      />
    </SearchBar>
  );
};
