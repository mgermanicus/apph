import { render, waitFor } from '@testing-library/react';
import * as React from 'react';
import { PhotoTable } from '../../static/components/PhotoTable';
import { wrapper } from '../utils/components/CustomWrapper';

jest.mock('react-i18next', () => ({
  // this mock makes sure any components using the translate hook can use it without a warning being shown
  useTranslation: () => {
    return {
      t: (str: string) => str
    };
  }
}));

describe('Tests du composant PhotoTable.tsx', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });
  it('Tests array display when data are send', async () => {
    //GIVEN
    const data = [
      {
        id: 3,
        title: 'photo1',
        description: 'photo test 1',
        creationDate: new Date(),
        modificationDate: new Date(),
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
        modificationDate: new Date(),
        shootingDate: new Date(),
        size: 1300.0,
        tags: [{ name: 'img2' }],
        url: 'fake url',
        format: 'png'
      }
    ];
    //WHEN
    const tree = render(
      <PhotoTable
        data={data}
        selected={[2]}
        loading={false}
        totalSize={2}
        page={0}
        pageSize={5}
        setPage={() => 0}
        setPageSize={() => 5}
      />,
      {
        wrapper
      }
    );
    //THEN
    expect(tree.getAllByRole(/row/)[2].classList.contains('Mui-selected')).toBe(
      true
    );
    expect(tree.getByText(/photo1/)).toBeInTheDocument();
    expect(tree.getByText(/photo2/)).toBeInTheDocument();
    await waitFor(() => {
      expect(
        tree.getAllByRole('button', { name: /download-photo/i })[0]
      ).toBeInTheDocument();
    });
  });
});
