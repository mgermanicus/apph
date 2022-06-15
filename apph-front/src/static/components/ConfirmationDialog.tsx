import {
  Button,
  Dialog,
  DialogActions,
  DialogContent,
  DialogContentText,
  DialogTitle
} from '@mui/material';
import { useTranslation } from 'react-i18next';

type Props = {
  open: boolean;
  message?: string;
  title?: string;
  onConfirm?: () => void;
  onCancel?: () => void;
};
export const ConfirmationDialog = ({
  open,
  message,
  title,
  onConfirm,
  onCancel
}: Props) => {
  const { t } = useTranslation();
  return (
    <Dialog open={open} sx={{ py: '20vh' }}>
      <DialogTitle>{t(title || '')}</DialogTitle>
      <DialogContent></DialogContent>
      <DialogContentText>{t(message || '')}</DialogContentText>
      <DialogActions>
        <Button color="error" onClick={onCancel}>
          {t('action.cancel')}
        </Button>
        <Button color="primary" onClick={onConfirm}>
          {t('action.continue')}
        </Button>
      </DialogActions>
    </Dialog>
  );
};
