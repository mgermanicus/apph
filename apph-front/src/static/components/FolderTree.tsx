import { IFolder } from '../../utils/types/Folder';
import { TreeItem } from '@mui/lab';
import { Alert } from '@mui/material';
import * as React from 'react';

export const FolderTree = ({
  folder,
  key
}: {
  folder: IFolder | null;
  key?: number;
}): JSX.Element => {
  if (folder) {
    return (
      <TreeItem
        nodeId={folder.id.toString()}
        key={folder.id.toString() + key}
        label={folder.name}
      >
        {Array.isArray(folder.childrenFolders) && folder.childrenFolders.length
          ? folder.childrenFolders.map((child, key) => (
              <FolderTree folder={child} key={key} />
            ))
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
