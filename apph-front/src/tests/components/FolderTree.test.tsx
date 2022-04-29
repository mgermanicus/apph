import * as React from 'react';
import { triggerRequestFailure, triggerRequestSuccess } from '../utils/library';
import { render, screen } from '@testing-library/react';
import { MyFoldersContainer } from '../../static/containers/MyFoldersContainer';

describe('Folder Tree Tests', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('render folder tree with success', () => {
    //GIVEN
    triggerRequestSuccess(
      '{"id":1,"version":0,"name":"Elie_root","parentFolderId":null,"childrenFolders":[{"id":2,"version":0,"name":"Elie_child_1","parentFolderId":1,"childrenFolders":[]},{"id":3,"version":0,"name":"Elie_child_2","parentFolderId":1,"childrenFolders":[]}]}'
    );
    //WHEN
    render(<MyFoldersContainer />);
    //THEN
    expect(screen.getByText(/Elie_root/)).toBeInTheDocument();
  });

  it('render folder tree with error', () => {
    //GIVEN
    triggerRequestFailure('{"message": "User not found."}');
    //WHEN
    render(<MyFoldersContainer />);
    //THEN
    expect(screen.getByText(/User not found./)).toBeInTheDocument();
  });
});