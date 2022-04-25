import { IFolder } from '../../utils/types/Folder';
import { TreeItem } from '@mui/lab';
import { Alert } from '@mui/material';
import * as React from 'react';

export const FolderTree = ({
  folder
}: {
  folder: IFolder | null;
}): JSX.Element => {
  if (folder) {
    return (
      <TreeItem nodeId={folder.id} key={folder.id} label={folder.name}>
        {Array.isArray(folder.childrenFolders) && folder.childrenFolders.length
          ? folder.childrenFolders.map((child) => <FolderTree folder={child} />)
          : null}
      </TreeItem>
    );
  } else {
    return (
      <Alert sx={{ mb: 2 }} severity="error">
        Folder is null !
      </Alert>
    );
  }
};
