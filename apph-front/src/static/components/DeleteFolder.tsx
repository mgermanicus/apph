import React, { SyntheticEvent, useEffect, useState } from 'react';
import {
  AlertColor,
  Box,
  Button,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  Tooltip
} from '@mui/material';
import { useTranslation } from 'react-i18next';
import { Delete } from '@mui/icons-material';
import { AlertSnackbar } from './AlertSnackbar';
import ExpandMore from '@mui/icons-material/ExpandMore';
import ChevronRight from '@mui/icons-material/ChevronRight';
import { FolderTree } from './FolderTree';
import { TreeView } from '@mui/lab';
import { IFolder } from '../../utils';
import { FolderService } from '../../services/FolderService';

export const DeleteFolder = ({
  selectedFolderId,
  rootFolderId,
  refreshFolder
}: {
  selectedFolderId: string;
  rootFolderId?: string;
  refreshFolder: () => void;
}): JSX.Element => {
  const [dialogVisible, setDialogVisible] = useState<boolean>(false);
  const [snackbarMessage, setSnackbarMessage] = useState<string>('');
  const [snackSeverity, setSnackSeverity] = useState<AlertColor>('error');
  const [snackbarOpen, setSnackbarOpen] = useState<boolean>(false);
  const [confirmDelete, setConfirmDelete] = useState<boolean>(false);
  const [confirmMoveContent, setConfirmMoveContent] = useState<boolean>(false);
  const [rootFolder, setRootFolder] = useState<IFolder | null>(null);
  const [selectedFolder, setSelectedFolder] = useState<string>('');
  const { t } = useTranslation();

  useEffect(() => {
    FolderService.getFolders(
      (parentFolder: IFolder) => {
        setRootFolder(parentFolder);
        setSelectedFolder(parentFolder.id?.toString());
      },
      (error: string) => {
        setSnackbarMessage(error);
        setSnackbarOpen(true);
      },
      rootFolderId || '-1'
    );
  }, []);

  const deleteFolder = (dstFolderId?: string) => {
    FolderService.deleteFolder(
      (successMessage: string) => {
        setDialogVisible(false);
        setSnackSeverity('success');
        setSnackbarMessage(successMessage);
        setSnackbarOpen(true);
        //TODO this refresh method is not working very well... Look for how to refresh a TreeView
        refreshFolder();
        setTimeout(() => setSnackbarOpen(false), 2000);
      },
      (errorMessage: string) => {
        setSnackbarMessage(errorMessage);
        setSnackSeverity('error');
        setSnackbarOpen(true);
      },
      selectedFolderId,
      dstFolderId
    );
  };

  const handleOpenDialog = () => {
    setConfirmDelete(false);
    setConfirmMoveContent(false);
    setSnackbarMessage('');
    setSnackbarOpen(false);
    setDialogVisible(true);
  };

  const handleCloseDialog = () => {
    setDialogVisible(false);
  };

  const handleConfirmDelete = () => {
    setConfirmDelete(true);
  };

  const handleConfirmMove = () => {
    if (selectedFolderId.toString() === rootFolder?.id.toString()) {
      setSnackbarMessage('folder.error.moveFolder');
      setSnackSeverity('warning');
      setSnackbarOpen(true);
    } else if (rootFolder == null) {
      setSnackbarMessage('folder.error.parentNotExist');
      setSnackSeverity('error');
      setSnackbarOpen(true);
    } else {
      setSelectedFolder(rootFolder.id.toString());
      setConfirmMoveContent(true);
    }
  };

  const handleDelete = () => {
    deleteFolder();
  };

  const handleMove = () => {
    deleteFolder(selectedFolder);
  };

  const dialogContent = () => {
    if (!confirmDelete) return t('folder.deleteFolder.confirmDelete');
    if (!confirmMoveContent) return t('folder.deleteFolder.confirmMoveContent');
    return (
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
    );
  };

  const dialogAction = () => {
    if (!confirmDelete) {
      return (
        <Button color="primary" onClick={handleConfirmDelete}>
          {t('action.continue')}
        </Button>
      );
    }
    if (!confirmMoveContent) {
      return (
        <>
          <Button color="primary" onClick={handleConfirmMove}>
            {t('action.move')}
          </Button>
          <Button color="primary" onClick={handleDelete}>
            {t('action.delete')}
          </Button>
        </>
      );
    }

    return (
      <Button color="primary" onClick={handleMove}>
        {t('action.confirm')}
      </Button>
    );
  };

  return (
    <Box sx={{ m: 1 }}>
      <Tooltip title={t('folder.deleteFolder.dialogButton')}>
        <Button
          variant="outlined"
          color="error"
          onClick={handleOpenDialog}
          aria-label="delete-folder"
        >
          <Delete />
        </Button>
      </Tooltip>
      <Dialog open={dialogVisible} onClose={handleCloseDialog}>
        <DialogTitle sx={{ fontWeight: 'bold' }}>
          {t('folder.moveTo')}
        </DialogTitle>
        <DialogContent
          sx={
            confirmMoveContent
              ? {
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
                }
              : {}
          }
        >
          {dialogContent()}
        </DialogContent>
        <DialogActions>
          <Button color="error" onClick={handleCloseDialog}>
            {t('action.cancel')}
          </Button>
          {dialogAction()}
        </DialogActions>
      </Dialog>
      <AlertSnackbar
        open={snackbarOpen}
        severity={snackSeverity}
        message={t(snackbarMessage)}
        onClose={setSnackbarOpen}
      />
    </Box>
  );
};
