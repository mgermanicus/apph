export interface IFolder {
  id: string;
  version: string;
  name: string;
  parentFolderId: string;
  childrenFolders: IFolder[];
}
