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
      tags: [{ name: 'tag' }]
    };
    const newPhotoDetails = {
      title: 'New Title',
      description: 'New Description',
      shootingDate: new Date(2000, 0),
      tags: [{ name: 'new tag1' }, { name: 'new tag 2' }]
    };
    render(<EditPhotoDetails id={0} {...photoDetails} />);
    //WHEN
    clickButton(/Modifier/);
    fillText(/Titre/, newPhotoDetails.title);
    fillText(/Description/, newPhotoDetails.description);
    fillDate(newPhotoDetails.shootingDate);
    fillTags(newPhotoDetails.tags);
    clickButton(/Valider/);
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
      tags: [{ name: 'tag' }]
    };
    render(<EditPhotoDetails id={0} {...photoDetails} />);
    //WHEN
    clickButton(/Modifier/);
    clickButton(/Valider/);
    //THEN
    expect(spyRequestFunction).toBeCalledWith(
      '/photo/editInfos',
      expect.anything(),
      expect.anything(),
      expect.anything()
    );
  });
});
