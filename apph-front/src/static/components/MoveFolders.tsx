import { AlertColor, Box, Button } from '@mui/material';
import * as React from 'react';
import { useState } from 'react';
import { AlertSnackbar } from './AlertSnackbar';

export const MoveFolders = ({
  selectedFolder
}: {
  selectedFolder: string;
}): JSX.Element => {
  const [errorMessage, setErrorMessage] = useState('');
  const [snackMessage, setSnackMessage] = useState<string>('');
  const [snackSeverity, setSnackSeverity] = useState<AlertColor>('info');
  const [snackbarOpen, setSnackbarOpen] = useState<boolean>(false);
  return (
    <Box component="div">
      <Button>Déplacer le dossier</Button>
      <AlertSnackbar
        open={snackbarOpen}
        severity={snackSeverity}
        message={snackMessage}
        onClose={setSnackbarOpen}
      />
    </Box>
  );
};
