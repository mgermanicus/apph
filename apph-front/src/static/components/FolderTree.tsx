import { IFolder } from '../../utils';
import { TreeItem } from '@mui/lab';
import { Alert } from '@mui/material';
import * as React from 'react';
import { useEffect, useState } from 'react';
import { FolderService } from '../../services/FolderService';
import { updateFolder } from '../../utils/folder';
import { useTranslation } from 'react-i18next';

export const FolderTree = ({
  folder
}: {
  folder: IFolder | null;
}): JSX.Element => {
  const { t } = useTranslation();
  const [errorMessage, setErrorMessage] = useState('folder.error.nullFolder');
  const [data, setData] = useState(folder);

  useEffect(() => {
    if (folder) handleGetChildrenFolders(folder).catch(console.error);
  }, [folder]);

  const handleGetChildrenFolders = async (parent: IFolder) => {
    await FolderService.getFolders(
      (folder: IFolder) => {
        if (folder.id === data?.id) {
          setData(folder);
        } else {
          if (data) {
            const updatedData = updateFolder(data, folder);
            setData(updatedData);
          }
        }
      },
      (error: string) => {
        setErrorMessage(error);
      },
      parent.id.toString()
    );
  };

  if (data) {
    return (
      <TreeItem nodeId={data.id.toString()} key={data.id} label={data.name}>
        {data.childrenFolders?.length > 0 &&
          data.childrenFolders.map((child) => (
            <FolderTree folder={child} key={parseInt(child.id)} />
          ))}
      </TreeItem>
    );
  } else {
    return (
      <Alert sx={{ mb: 2 }} severity="error">
        {t(errorMessage)}
      </Alert>
    );
  }
};
