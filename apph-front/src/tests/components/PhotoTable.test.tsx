import { render, waitFor } from '@testing-library/react';
import { screen } from '@testing-library/dom';
import * as React from 'react';
import { PhotoTable } from '../../static/components/PhotoTable';
import { wrapper } from '../utils/components/CustomWrapper';
import { IPagination } from '../../utils';

describe('Tests du composant PhotoTable.tsx', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });
  it('Tests array display when data are send', () => {
    //GIVEN
    const getPhotosSuccess = (
      pageSize: number,
      page: number,
      handleSuccess: (pagination: IPagination) => void
    ) => {
      handleSuccess({
        photoList: [
          {
            id: 3,
            title: 'photo1',
            description: 'photo test 1',
            creationDate: new Date(),
            shootingDate: new Date(),
            size: 1300.0,
            tags: [{ name: 'img1' }],
            url: 'fake url',
            format: 'png'
          },
          {
            id: 2,
            title: 'photo2',
            description: 'photo test 2',
            creationDate: new Date(),
            shootingDate: new Date(),
            size: 1300.0,
            tags: [{ name: 'img2' }],
            url: 'fake url',
            format: 'png'
          }
        ],
        totalSize: 3
      });
    };
    //WHEN
    const tree = render(
      <PhotoTable getPhotos={getPhotosSuccess} selected={[2]} />,
      { wrapper }
    );
    //THEN
    expect(tree).toMatchSnapshot();
    expect(tree.getAllByRole(/row/)[2].classList.contains('Mui-selected')).toBe(
      true
    );
    expect(tree.getByText(/photo1/)).toBeInTheDocument();
    expect(tree.getByText(/photo2/)).toBeInTheDocument();
  });

  it('Test with error server', () => {
    //GIVEN
    const getPhotosFailure = (
      pageSize: number,
      page: number,
      handleSuccess: (pagination: IPagination) => void,
      handleError: (errorMessage: string) => void
    ) => {
      handleError('Argument illégal.');
    };
    //WHEN
    render(<PhotoTable getPhotos={getPhotosFailure} />, { wrapper });
    //THEN
    expect(screen.getByText(/Argument illégal./));
  });

  test('Tests button display when grid rendered', async () => {
    //GIVEN
    const getPhotosSuccess = (
      pageSize: number,
      page: number,
      handleSuccess: (pagination: IPagination) => void
    ) => {
      handleSuccess({
        photoList: [
          {
            id: 2,
            title: 'photo2',
            description: 'photo test 2',
            creationDate: new Date(),
            shootingDate: new Date(),
            size: 1300.0,
            tags: [{ name: 'img2' }],
            url: 'fake url',
            format: 'png'
          }
        ],
        totalSize: 3
      });
    };
    // When
    render(<PhotoTable getPhotos={getPhotosSuccess} />, { wrapper });
    // Then
    await waitFor(() => {
      expect(
        screen.getAllByRole('button', { name: /download-photo/i })[0]
      ).toBeInTheDocument();
    });
  });
});
