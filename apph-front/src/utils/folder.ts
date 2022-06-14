import { IFolder } from './types';

export const updateFolder = (
  parentFolder: IFolder,
  folder: IFolder
): IFolder => {
  for (const f of parentFolder.childrenFolders) {
    if (f.id === folder.id) {
      f.childrenFolders = folder.childrenFolders;
    } else {
      updateFolder(f, folder);
    }
  }
  return parentFolder;
};
