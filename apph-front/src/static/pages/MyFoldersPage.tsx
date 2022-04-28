import * as React from 'react';
import { SyntheticEvent, useEffect, useState } from 'react';
import { IFolder } from '../../utils/types/Folder';
import { FolderService } from '../../services/FolderService';
import CircularProgress from '@mui/material/CircularProgress';
import TreeView from '@mui/lab/TreeView';
import { FolderTree } from '../components/FolderTree';
import ExpandMore from '@mui/icons-material/ExpandMore';
import ChevronRight from '@mui/icons-material/ChevronRight';
import { Alert } from '@mui/material';

export const MyFoldersPage = (): JSX.Element => {
  const [rootFolder, setRootFolder] = useState<IFolder | null>(null);
  const [selectedFolder, setSelectedFolder] = useState<string>('');
  const [errorMessage, setErrorMessage] = useState('');
  const [loading, setLoading] = useState<boolean>(true);
  useEffect(() => {
    getFolders().catch(console.error);
  }, []);

  const getFolders = async () => {
    await FolderService.getFolders(
      //TODO replace by the userId or change by the token
      5,
      (parentFolder) => {
        setRootFolder(parentFolder);
        setSelectedFolder(parentFolder.id);
        setLoading(false);
      },
      (error: string) => {
        setErrorMessage(error);
        setLoading(false);
      }
    );
  };

  if (loading) {
    //TODO replace by a real loading page
    return <CircularProgress />;
  } else if (errorMessage) {
    return (
      <Alert sx={{ mb: 2 }} severity="error">
        {errorMessage}
      </Alert>
    );
  } else {
    return (
      <div
        style={{
          display: 'flex',
          marginTop: '70px'
        }}
      >
        <TreeView
          defaultCollapseIcon={<ExpandMore />}
          defaultExpandIcon={<ChevronRight />}
          selected={selectedFolder}
          onNodeSelect={(_event: SyntheticEvent, node: string) => {
            setSelectedFolder(node);
          }}
          sx={{
            overflowX: 'hidden',
            width: '30%'
          }}
        >
          <FolderTree folder={rootFolder} />
        </TreeView>
        {/*TODO display folder's content here*/}
        <div
          style={{
            width: '70%'
          }}
        >
          Selected folder's id: {selectedFolder}
        </div>
      </div>
    );
  }
};
