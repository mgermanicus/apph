import { IFolder } from '../../utils/types/Folder';
import { TreeItem } from '@mui/lab';

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
    //TODO handling error when folder is null
    console.error('Folder is null !');
    return <>Error</>;
  }
};
