import { AlertColor, Snackbar } from '@mui/material';
import React from 'react';
import MuiAlert, { AlertProps } from '@mui/material/Alert';
import { useTranslation } from 'react-i18next';

const Alert = React.forwardRef<HTMLDivElement, AlertProps>(function Alert(
  props,
  ref
) {
  return <MuiAlert elevation={6} ref={ref} variant="filled" {...props} />;
});

type Props = {
  open: boolean;
  severity?: AlertColor;
  message: string;
  onClose: (b: boolean) => void;
};

export const AlertSnackbar = ({ open, severity, message, onClose }: Props) => {
  const handleSnackbarClose = (
    event?: React.SyntheticEvent | Event,
    reason?: string
  ) => {
    if (reason === 'clickaway') return;
    onClose(false);
  };
  const { t } = useTranslation();

  return (
    <Snackbar
      open={open}
      autoHideDuration={6000}
      anchorOrigin={{ vertical: 'bottom', horizontal: 'center' }}
      onClose={handleSnackbarClose}
    >
      <Alert onClose={handleSnackbarClose} severity={severity}>
        {t(message)}
      </Alert>
    </Snackbar>
  );
};
