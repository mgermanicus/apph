import { render } from '@testing-library/react';
import { screen } from '@testing-library/dom';
import PhotoDetails from '../../static/components/PhotoDetails';
import { clickButton } from '../utils';
import { ITag } from '../../utils';

describe('Test du composant PhotoDetails', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('check if datas are render correctly', () => {
    //GIVEN
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
    render(
      <PhotoDetails
        photoId={mockPhoto.id}
        photoSrc={mockPhoto.src}
        title={mockPhoto.title}
        description={mockPhoto.description}
        creationDate={mockPhoto.creationDate}
        modificationDate={mockPhoto.modificationDate}
        shootingDate={mockPhoto.shootingDate}
        size={mockPhoto.size}
        tags={mockPhoto.tags}
        format={mockPhoto.format}
        clickType="button"
        updateData={() => {
          return;
        }}
      />
    );
    //WHEN
    clickButton(/photo-detail/i);
    //THEN
    expect(screen.getAllByText(/testTitle/)).toBeInstanceOf(Array);
    expect(screen.getByText(/testDescription/)).toBeInTheDocument();
    expect(screen.getByText(/testTag/)).toBeInTheDocument();
  });
});
