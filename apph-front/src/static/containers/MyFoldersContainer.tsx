import { SyntheticEvent, useEffect, useState } from 'react';
import { IFolder } from '../../utils/types/Folder';
import { FolderService } from '../../services/FolderService';
import CircularProgress from '@mui/material/CircularProgress';
import TreeView from '@mui/lab/TreeView';
import { FolderTree } from '../components/FolderTree';
import ExpandMore from '@mui/icons-material/ExpandMore';
import ChevronRight from '@mui/icons-material/ChevronRight';

export const MyFoldersContainer = (): JSX.Element => {
  const [rootFolder, setRootFolder] = useState<IFolder | null>(null);
  const [selectedFolder, setSelectedFolder] = useState<string>('');
  const [loading, setLoading] = useState<boolean>(true);
  useEffect(() => {
    getFolders().catch(console.error);
  }, []);

  const getFolders = async () => {
    const parentFolder = await FolderService.getFolders(1);
    if (parentFolder) {
      setRootFolder(parentFolder);
      setSelectedFolder(parentFolder.id);
      setLoading(false);
    } else {
      console.log('Réponse nulle !');
    }
  };

  return (
    <>
      {loading ? (
        //TODO replace by a real loading page
        <CircularProgress />
      ) : (
        <div
          style={{
            display: 'flex'
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
      )}
    </>
  );
};
