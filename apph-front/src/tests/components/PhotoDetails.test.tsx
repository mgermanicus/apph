import { render } from '@testing-library/react';
import { screen } from '@testing-library/dom';
import PhotoDetails from '../../static/components/PhotoDetails';
import { clickButton } from '../utils';

describe('Test du composant PhotoDetails', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('check if datas are render correctly', () => {
    //GIVEN
    const mockPhoto = {
      src: 'https://i.pinimg.com/originals/a2/39/b5/a239b5b33d145fcab7e48544b81019da.jpg',
      title: 'testTitle',
      description: 'testDescription',
      creationDate: new Date(),
      shootingDate: new Date(),
      size: 0,
      tags: ['testTag']
    };
    render(
      <PhotoDetails
        photoSrc={mockPhoto.src}
        title={mockPhoto.title}
        description={mockPhoto.description}
        creationDate={mockPhoto.creationDate}
        shootingDate={mockPhoto.shootingDate}
        size={mockPhoto.size}
        tags={mockPhoto.tags}
      />
    );
    //WHEN
    clickButton(/Détails/);
    //THEN
    expect(screen.getAllByText(/testTitle/)).toBeInstanceOf(Array);
    expect(screen.getByText(/testDescription/)).toBeInTheDocument();
    expect(screen.getByText(/testTag/)).toBeInTheDocument();
  });
});
