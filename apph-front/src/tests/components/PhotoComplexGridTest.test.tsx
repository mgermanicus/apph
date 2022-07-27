import { render } from '@testing-library/react';
import { PhotoComplexGrid } from '../../static/components/PhotoComplexGrid';
import { screen } from '@testing-library/dom';
import { ITable, ITag } from '../../utils';
import { renderWithWrapper } from '../utils';

describe('Photo complex grid test', () => {
  const mockPhoto: ITable = {
    location: { address: 'Paris, France', position: { lat: 0, lng: 0 } },
    id: 0,
    url: 'https://i.pinimg.com/originals/a2/39/b5/a239b5b33d145fcab7e48544b81019da.jpg',
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

  it('test text shown correctly', async () => {
    //WHEN
    renderWithWrapper(<PhotoComplexGrid photo={mockPhoto} />);
    //THEN
    expect(screen.getByText(/testTitle/)).toBeInTheDocument();
  });
});
