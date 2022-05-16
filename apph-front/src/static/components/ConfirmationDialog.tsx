import {
  Button,
  Dialog,
  DialogActions,
  DialogContent,
  DialogContentText,
  DialogTitle
} from '@mui/material';

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
  return (
    <Dialog open={open} sx={{ py: '20vh' }}>
      <DialogTitle>{title}</DialogTitle>
      <DialogContent>
        <DialogContentText>{message}</DialogContentText>
      </DialogContent>
      <DialogActions>
        <Button color="primary" onClick={onConfirm}>
          Continuer
        </Button>
        <Button color="error" onClick={onCancel}>
          Annuler
        </Button>
      </DialogActions>
    </Dialog>
  );
};
