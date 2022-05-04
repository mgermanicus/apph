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
import { IFolder } from '../../utils/types/Folder';

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

  const handleSubmit = () => {
    if (folderName === '') {
      setErrorMessage('Le nom du dossier ne peut pas être vide.');
    } else {
      setLoading(true);
      FolderService.createFolder(
        folderName,
        selected,
        (folder: IFolder) => {
          setRootFolder(folder);
          setLoading(false);
          setShowModal(false);
        },
        (error: string) => {
          setErrorMessage(error);
          setLoading(false);
        }
      );
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
        Créer un dossier
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
          Création d'un dossier
        </DialogTitle>
        <DialogContent>
          <DialogContentText sx={{ color: 'black' }}>
            Entrez le nom de dossier:
          </DialogContentText>
          <TextField
            autoFocus
            margin="dense"
            value={folderName}
            onChange={(event) => {
              setFolderName(event.currentTarget.value);
            }}
            id="folder-name"
            autoComplete="Nouveau Dossier"
            label="Nom du Dossier"
            size="small"
            disabled={loading}
            onKeyDown={(event) => {
              if (event.key === 'Enter') {
                event.preventDefault();
                handleSubmit();
              }
            }}
          />
          <DialogContentText
            sx={{ color: 'red', fontSize: 'small' }}
            hidden={!errorMessage}
          >
            {errorMessage}
          </DialogContentText>
        </DialogContent>
        <DialogActions>
          <Button
            onClick={(event) => {
              event.preventDefault();
              handleSubmit();
            }}
            disabled={loading}
          >
            {loading ? <CircularProgress /> : <>Créer</>}
          </Button>
        </DialogActions>
      </Dialog>
    </>
  );
};
