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

  const handleSubmit = (event: React.MouseEvent<HTMLButtonElement>) => {
    event.preventDefault();
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
        <DialogTitle>Création d'un dossier</DialogTitle>
        <DialogContent>
          <DialogContentText>Entrez le nom de dossier:</DialogContentText>
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
          />
          <DialogContentText
            sx={{ color: 'red', fontSize: 'small' }}
            hidden={!errorMessage}
          >
            {errorMessage}
          </DialogContentText>
        </DialogContent>
        <DialogActions>
          <Button onClick={(event) => handleSubmit(event)} disabled={loading}>
            {loading ? <CircularProgress /> : <>Créer</>}
          </Button>
        </DialogActions>
      </Dialog>
    </>
  );
};
