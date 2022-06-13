import { IFolder } from '../../utils';
import { TreeItem } from '@mui/lab';
import { Alert } from '@mui/material';
import * as React from 'react';
import { useTranslation } from 'react-i18next';

export const FolderTree = ({
  folder
}: {
  folder: IFolder | null;
  key?: number;
}): JSX.Element => {
  const { t } = useTranslation();
  if (folder) {
    return (
      <TreeItem
        nodeId={folder.id.toString()}
        key={folder.id}
        label={folder.name}
      >
        {!!folder.childrenFolders?.length &&
          folder.childrenFolders.map((child) => (
            <FolderTree folder={child} key={parseInt(child.id)} />
          ))}
      </TreeItem>
    );
  } else {
    return (
      <Alert sx={{ mb: 2 }} severity="error">
        {t('folder.error.nullFolder')}
      </Alert>
    );
  }
};
