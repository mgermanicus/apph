import { render } from '@testing-library/react';
import { screen } from '@testing-library/dom';
import { PhotoDetails } from '../../static/components/PhotoDetails';
import {
  clickButton,
  triggerRequestFailure,
  triggerRequestSuccess
} from '../utils';
import { ITag } from '../../utils';
import React from 'react';

describe('Test du composant PhotoDetails', () => {
  const mockPhoto = {
    id: 0,
    src: 'https://i.pinimg.com/originals/a2/39/b5/a239b5b33d145fcab7e48544b81019da.jpg',
    title: 'testTitle',
    description: 'testDescription',
    creationDate: new Date(),
    modificationDate: new Date(),
    shootingDate: new Date(),
    size: 0,
    format: 'jpeg',
    tags: [{ id: 1, name: 'testTag' }] as ITag[]
  };

  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('check if datas are render correctly', () => {
    //GIVEN
    render(
      <PhotoDetails
        details={{
          photoId: 0,
          photoSrc: mockPhoto.src,
          title: mockPhoto.title,
          description: mockPhoto.description,
          creationDate: mockPhoto.creationDate,
          modificationDate: mockPhoto.modificationDate,
          shootingDate: mockPhoto.shootingDate,
          size: mockPhoto.size,
          tags: mockPhoto.tags,
          format: mockPhoto.format,
          fromFolders: true
        }}
        updateData={jest.fn()}
        refresh={jest.fn()}
        clickType="button"
      />
    );
    //WHEN
    clickButton(/photo-detail/i);
    //THEN
    expect(screen.getAllByText(/testTitle/)).toBeInstanceOf(Array);
    expect(screen.getByText(/testDescription/)).toBeInTheDocument();
    expect(screen.getByText(/testTag/)).toBeInTheDocument();
    expect(screen.getByText(/Supprimer depuis le dossier/)).toBeInTheDocument();
  });

  it('remove photo from folder', () => {
    //GIVEN
    triggerRequestSuccess(
      '{"id": 0, "title": "titre", "description": "Description success", "folderId": -1}'
    );
    render(
      <PhotoDetails
        details={{
          photoId: 0,
          photoSrc: mockPhoto.src,
          title: mockPhoto.title,
          description: mockPhoto.description,
          creationDate: mockPhoto.creationDate,
          modificationDate: mockPhoto.modificationDate,
          shootingDate: mockPhoto.shootingDate,
          size: mockPhoto.size,
          tags: mockPhoto.tags,
          format: mockPhoto.format,
          fromFolders: true,
          setSnackMessage: jest.fn(),
          setSnackSeverity: jest.fn(),
          setSnackbarOpen: jest.fn(),
          setRefresh: jest.fn()
        }}
        updateData={jest.fn()}
        refresh={jest.fn()}
        clickType="button"
      />
    );
    //WHEN
    clickButton(/photo-detail/i);
    clickButton(/Supprimer depuis le dossier/);
    clickButton(/Continuer/);
    triggerRequestSuccess(
      '{"statusCode":1, "message: "Suppression effectuée avec succès""}'
    );

    setTimeout(async () => {
      expect(
        screen.getByText(/Suppression effectuée avec succès/)
      ).toBeInTheDocument();
    }, 1000);
  });

  it('remove photo from folder trigger error', () => {
    //GIVEN
    triggerRequestFailure(
      '{"id": 0, "title": "titre", "description": "Description success", "folderId": -1}'
    );
    render(
      <PhotoDetails
        details={{
          photoId: 42,
          photoSrc: mockPhoto.src,
          title: mockPhoto.title,
          description: mockPhoto.description,
          creationDate: mockPhoto.creationDate,
          modificationDate: mockPhoto.modificationDate,
          shootingDate: mockPhoto.shootingDate,
          size: mockPhoto.size,
          tags: mockPhoto.tags,
          format: mockPhoto.format,
          fromFolders: true,
          setSnackMessage: jest.fn(),
          setSnackSeverity: jest.fn(),
          setSnackbarOpen: jest.fn(),
          setRefresh: jest.fn()
        }}
        updateData={jest.fn()}
        refresh={jest.fn()}
        clickType="button"
      />
    );
    //WHEN
    clickButton(/photo-detail/i);
    clickButton(/Supprimer depuis le dossier/);
    clickButton(/Continuer/);
    triggerRequestFailure(
      '{"statusCode":0, "message: "La photo n\'existe pas""}'
    );

    setTimeout(async () => {
      expect(screen.getByText(/La photo n'existe pas/)).toBeInTheDocument();
    }, 1000);
  });

  it('OnClick Annuler', () => {
    //GIVEN
    render(
      <PhotoDetails
        details={{
          photoId: 0,
          photoSrc: mockPhoto.src,
          title: mockPhoto.title,
          description: mockPhoto.description,
          creationDate: mockPhoto.creationDate,
          modificationDate: mockPhoto.modificationDate,
          shootingDate: mockPhoto.shootingDate,
          size: mockPhoto.size,
          tags: mockPhoto.tags,
          format: mockPhoto.format,
          fromFolders: true,
          setSnackMessage: jest.fn(),
          setSnackSeverity: jest.fn(),
          setSnackbarOpen: jest.fn(),
          setRefresh: jest.fn()
        }}
        updateData={jest.fn()}
        refresh={jest.fn()}
        clickType="button"
      />
    );
    //WHEN
    clickButton(/photo-detail/i);
    clickButton(/Supprimer depuis le dossier/);
    clickButton(/Annuler/);

    expect(screen.getAllByText(/testTitle/)).toBeInstanceOf(Array);
    expect(screen.getByText(/testDescription/)).toBeInTheDocument();
    expect(screen.getByText(/testTag/)).toBeInTheDocument();
    expect(screen.getByText(/Supprimer depuis le dossier/)).toBeInTheDocument();
  });
});
