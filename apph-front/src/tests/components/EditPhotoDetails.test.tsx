import { EditPhotoDetails } from '../../static/components/EditPhotoDetails';
import { render } from '@testing-library/react';
import {
  clickButton,
  fakeRequest,
  fillDate,
  fillTags,
  fillText
} from '../utils';

describe('test EditPhotoDetails', () => {
  beforeEach(() => jest.clearAllMocks());
  it('tests when user edits everything', () => {
    //GIVEN
    const spyRequestFunction = fakeRequest({
      '/tag/': { body: '[{"id":"0","name":"tag","version":0}]' },
      '/photo/editInfos': { body: JSON.stringify({ message: '' }) }
    });
    const photoDetails = {
      title: 'Title',
      description: 'Description',
      shootingDate: new Date(),
      location: { address: 'Paris, France', position: { lat: 0, lng: 0 } },
      tags: [{ name: 'tag' }]
    };
    const newPhotoDetails = {
      title: 'New Title',
      description: 'New Description',
      shootingDate: new Date(2000, 0),
      tags: [{ name: 'new tag1' }, { name: 'new tag 2' }]
    };
    render(<EditPhotoDetails id={0} {...photoDetails} onEdit={jest.fn()} />);
    //WHEN
    clickButton(/action.modify/);
    fillText(/photo.title/, newPhotoDetails.title);
    fillText(/photoTable.description/, newPhotoDetails.description);
    fillDate(newPhotoDetails.shootingDate);
    fillTags(newPhotoDetails.tags);
    clickButton(/action.confirm/);
    //THEN
    expect(spyRequestFunction).toBeCalledWith(
      '/photo/editInfos',
      expect.anything(),
      expect.anything(),
      expect.anything()
    );
  });

  it('tests when user edits nothing', () => {
    //GIVEN
    const spyRequestFunction = fakeRequest({
      '/tag/': { body: '[{"id":"0","name":"tag","version":0}]' },
      '/photo/editInfos': { body: JSON.stringify({ message: '' }) }
    });
    const photoDetails = {
      title: 'Title',
      description: 'Description',
      shootingDate: new Date(),
      location: { address: 'Paris, France', position: { lat: 0, lng: 0 } },
      tags: [{ name: 'tag' }]
    };
    render(<EditPhotoDetails id={0} {...photoDetails} onEdit={jest.fn()} />);
    //WHEN
    clickButton(/action.modify/);
    clickButton(/action.confirm/);
    //THEN
    expect(spyRequestFunction).toBeCalledWith(
      '/photo/editInfos',
      expect.anything(),
      expect.anything(),
      expect.anything()
    );
  });
});
