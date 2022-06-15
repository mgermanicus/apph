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
import * as React from 'react';
import { SyntheticEvent, useEffect, useState } from 'react';
import { TreeView } from '@mui/lab';
import { IFolder, IMessage } from '../../utils';
import { FolderService } from '../../services/FolderService';
import ExpandMore from '@mui/icons-material/ExpandMore';
import ChevronRight from '@mui/icons-material/ChevronRight';
import { FolderTree } from './FolderTree';
import PhotoService from '../../services/PhotoService';
import { AlertSnackbar } from './AlertSnackbar';
import { useTranslation } from 'react-i18next';

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
  const [rootFolder, setRootFolder] = useState<IFolder | null>(null);
  const [dialogVisible, setDialogVisible] = useState<boolean>(false);
  const [selectedFolder, setSelectedFolder] = useState<string>('');
  const [snackMessage, setSnackMessage] = useState<string>('');
  const [snackSeverity, setSnackSeverity] = useState<AlertColor>();
  const [resultMessage, setResultMessage] = useState<string[]>([]);
  const [snackbarOpen, setSnackbarOpen] = useState<boolean>(false);
  const [loading, setLoading] = useState<boolean>(false);
  const { t } = useTranslation();

  useEffect(() => {
    getFolders();
  }, []);

  const getFolders = () => {
    FolderService.getFolders(
      (parentFolder: IFolder) => {
        setRootFolder(parentFolder);
        setSelectedFolder(parentFolder.id.toString());
      },
      (error: string) => {
        setSnackMessage(error);
        setSnackSeverity('error');
        setSnackbarOpen(true);
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
        setSnackMessage(error.message);
        setSnackSeverity('error');
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
        setDialogVisible(true);
      } else {
        setSnackMessage('folder.error.parentNotExist');
        setSnackSeverity('error');
        setSnackbarOpen(true);
      }
    } else {
      setSnackMessage('photo.noneSelected');
      setSnackSeverity('warning');
      setSnackbarOpen(true);
    }
  };

  const handleCloseDialog = () => {
    setDialogVisible(false);
  };

  return (
    <Box sx={{ m: 1 }}>
      <Tooltip title={t('folder.moveInto')}>
        <Button
          variant="outlined"
          onClick={handleOpenDialog}
          aria-label="move-photo"
        >
          <DriveFileMove />
        </Button>
      </Tooltip>
      <Dialog open={dialogVisible} onClose={handleCloseDialog}>
        <DialogTitle sx={{ fontWeight: 'bold' }}>
          {t('folder.moveTo')}
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
                    {t(splitMessage[1])}
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
              {t('folder.move')}
            </Button>
          )}
        </DialogActions>
      </Dialog>
      <AlertSnackbar
        open={snackbarOpen}
        severity={snackSeverity}
        message={t(snackMessage)}
        onClose={setSnackbarOpen}
      />
    </Box>
  );
};
