import { FolderTree } from '../../static/components/FolderTree';
import { render, screen } from '@testing-library/react';
import { IFolder } from '../../utils';

describe('FolderTree test', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });
  it('should render', () => {
    //GIVEN
    const folder: IFolder = {
      id: '1',
      name: 'Elie_root',
      childrenFolders: [
        {
          id: '2',
          name: 'Elie_child',
          childrenFolders: [],
          parentFolderId: '1',
          version: '0'
        }
      ],
      parentFolderId: '',
      version: '0'
    };
    //WHEN
    render(<FolderTree folder={folder} />);
    //THEN
    expect(screen.getByText(/Elie_root/)).toBeInTheDocument();
  });

  it('should render folder null', () => {
    //WHEN
    render(<FolderTree folder={null} />);
    //THEN
    expect(screen.getByText(/folder.error.nullFolder/)).toBeInTheDocument();
  });
});
