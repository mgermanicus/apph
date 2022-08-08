import { useEffect, useState } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';
import { Box } from '@mui/material';
import { useTranslation } from 'react-i18next';
import AuthService from '../../services/AuthService';

export const ActiveUser = () => {
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();
  const [message, setMessage] = useState<string>('');
  const { t } = useTranslation();
  useEffect(() => {
    AuthService.activeUser(
      searchParams.get('token') || '',
      (successMessage) => {
        setMessage(successMessage);
        setTimeout(() => {
          navigate('/');
        }, 3000);
      },
      (errorMessage) => {
        setMessage(errorMessage);
      }
    );
  }, []);
  return (
    <Box component="div">
      <h1>{t(message)}</h1>
    </Box>
  );
};
