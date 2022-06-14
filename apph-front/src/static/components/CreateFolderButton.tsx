import Button from '@mui/material/Button';
import React, { Dispatch, SetStateAction, useState } from 'react';
import {
  CircularProgress,
  Dialog,
  DialogActions,
  DialogContent,
  DialogContentText,
  DialogTitle,
  TextField
} from '@mui/material';
import { FolderService } from '../../services/FolderService';
import { IFolder } from '../../utils';
import { useTranslation } from 'react-i18next';

export const CreateFolderButton = ({
  selected,
  setRootFolder
}: {
  selected: string;
  setRootFolder: Dispatch<SetStateAction<IFolder | null>>;
}): JSX.Element => {
  const [showModal, setShowModal] = useState<boolean>(false);
  const [folderName, setFolderName] = useState<string>('');
  const [errorMessage, setErrorMessage] = useState('');
  const [loading, setLoading] = useState<boolean>(false);
  const { t } = useTranslation();
  const handleSubmit = async () => {
    if (folderName === '') {
      setErrorMessage('folder.error.emptyFolder');
    } else {
      setLoading(true);
      await FolderService.createFolder(
        folderName,
        selected,
        (folder: IFolder) => {
          setRootFolder(folder);
          setShowModal(false);
        },
        (error: string) => {
          setErrorMessage(error);
        }
      );
      setLoading(false);
    }
  };

  return (
    <>
      <Button
        onClick={() => {
          setFolderName('');
          setShowModal(true);
        }}
      >
        {t('folder.createFolder')}
      </Button>
      <Dialog
        open={showModal}
        onClose={() => {
          if (!loading) {
            setShowModal(false);
          }
        }}
      >
        <DialogTitle sx={{ fontWeight: 'bold' }}>
          {t('folder.creation')}
        </DialogTitle>
        <DialogContent>
          <DialogContentText sx={{ color: 'black' }}>
            {t('folder.enterName')}
          </DialogContentText>
          <TextField
            autoFocus
            margin="dense"
            value={folderName}
            onChange={(event) => {
              setFolderName(event.currentTarget.value);
            }}
            id="folder-name"
            autoComplete={t('folder.new')}
            label={t('folder.name')}
            size="small"
            disabled={loading}
            inputProps={{ maxLength: 255 }}
            onKeyDown={async (event) => {
              if (event.key === 'Enter') {
                event.preventDefault();
                await handleSubmit();
              }
            }}
          />
          <DialogContentText
            sx={{ color: 'red', fontSize: 'small' }}
            hidden={!errorMessage}
          >
            {t(errorMessage)}
          </DialogContentText>
        </DialogContent>
        <DialogActions>
          <Button
            onClick={async (event) => {
              event.preventDefault();
              await handleSubmit();
            }}
            disabled={loading}
          >
            {loading ? <CircularProgress /> : <>{t('folder.create')}</>}
          </Button>
        </DialogActions>
      </Dialog>
    </>
  );
};
