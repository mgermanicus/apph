import {
  Alert,
  AlertColor,
  Box,
  Button,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  Stack,
  Tooltip
} from '@mui/material';
import { DriveFileMove } from '@mui/icons-material';
import { SyntheticEvent, useEffect, useState } from 'react';
import { TreeView } from '@mui/lab';
import { IFolder, IMessage } from '../../utils';
import { FolderService } from '../../services/FolderService';
import ExpandMore from '@mui/icons-material/ExpandMore';
import ChevronRight from '@mui/icons-material/ChevronRight';
import { FolderTree } from './FolderTree';
import * as React from 'react';
import PhotoService from '../../services/PhotoService';
import { AlertSnackbar } from './AlertSnackbar';

const stringToAlertColor = (status: string): AlertColor => {
  switch (status) {
    case 'success':
      return 'success';
    case 'error':
      return 'error';
    case 'warning':
      return 'warning';
    default:
      return 'info';
  }
};

export const MovePhoto = ({
  photoIds
}: {
  photoIds: number[];
}): JSX.Element => {
  const [dialogVisible, setDialogVisible] = useState<boolean>(false);
  const [rootFolder, setRootFolder] = useState<IFolder | null>(null);
  const [selectedFolder, setSelectedFolder] = useState<string>('');
  const [errorMessage, setErrorMessage] = useState<string>('');
  const [snackMessage, setSnackMessage] = useState<string>('');
  const [resultMessage, setResultMessage] = useState<string[]>([]);
  const [snackbarOpen, setSnackbarOpen] = useState<boolean>(false);
  const [loading, setLoading] = useState<boolean>(false);

  useEffect(() => {
    getFolders().catch(console.error);
  }, []);

  const getFolders = async () => {
    await FolderService.getFolders(
      (parentFolder: IFolder) => {
        setRootFolder(parentFolder);
        setSelectedFolder(parentFolder.id.toString());
      },
      (error: string) => {
        setErrorMessage(error);
      }
    );
  };

  const handleSubmit = () => {
    setLoading(false);
    PhotoService.movePhotos(
      photoIds,
      selectedFolder,
      (message: string[]) => {
        setResultMessage(message);
        setLoading(false);
      },
      (error: IMessage) => {
        setErrorMessage(error.message);
        setLoading(false);
      }
    );
  };

  const handleOpenDialog = () => {
    if (photoIds.length) {
      if (rootFolder != null) {
        setSelectedFolder(rootFolder.id.toString());
        setResultMessage([]);
        setSnackbarOpen(false);
        setErrorMessage('');
        setDialogVisible(true);
      } else {
        setSnackMessage('Dossier parent non existant.');
        setSnackbarOpen(true);
      }
    } else {
      setSnackMessage('Aucune photo sélectionnée');
      setSnackbarOpen(true);
    }
  };

  const handleCloseDialog = () => {
    setDialogVisible(false);
  };

  return (
    <Box sx={{ m: 1 }}>
      <Tooltip title="Déplacer dans le dossier">
        <Button variant="outlined" onClick={handleOpenDialog}>
          <DriveFileMove />
        </Button>
      </Tooltip>
      <Dialog open={dialogVisible} onClose={handleCloseDialog}>
        <DialogTitle sx={{ fontWeight: 'bold' }}>
          Déplacer vers un dossier
        </DialogTitle>
        <DialogContent
          sx={{
            width: {
              xs: 200,
              sm: 300,
              lg: 400,
              xl: 500
            },
            height: {
              xs: 200,
              sm: 300,
              lg: 400,
              xl: 500
            }
          }}
        >
          {resultMessage.length ? (
            <Stack spacing={0.5}>
              {resultMessage.map((message, index) => {
                const splitMessage = message.split(': ');
                return (
                  <Alert
                    key={index}
                    severity={stringToAlertColor(splitMessage[0])}
                  >
                    {splitMessage[1]}
                  </Alert>
                );
              })}
            </Stack>
          ) : (
            <TreeView
              defaultCollapseIcon={<ExpandMore />}
              defaultExpandIcon={<ChevronRight />}
              selected={selectedFolder}
              onNodeSelect={(_event: SyntheticEvent, node: string) => {
                setSelectedFolder(node);
              }}
              sx={{
                width: '100%',
                textAlign: 'start'
              }}
            >
              <FolderTree folder={rootFolder} />
            </TreeView>
          )}
        </DialogContent>
        <DialogContent sx={{ overflowY: 'unset' }} hidden={!errorMessage}>
          <Alert severity="error">{errorMessage}</Alert>
        </DialogContent>
        <DialogActions sx={{ justifyContent: 'center' }}>
          {resultMessage.length ? (
            <Button
              variant="text"
              sx={{ width: '100%' }}
              onClick={handleCloseDialog}
            >
              Ok
            </Button>
          ) : (
            <Button
              variant="text"
              sx={{ width: '100%' }}
              disabled={loading}
              onClick={handleSubmit}
            >
              Déplacer
            </Button>
          )}
        </DialogActions>
      </Dialog>
      <AlertSnackbar
        open={snackbarOpen}
        severity="warning"
        message={snackMessage}
        onClose={setSnackbarOpen}
      />
    </Box>
  );
};
