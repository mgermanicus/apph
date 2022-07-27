import { act, screen } from '@testing-library/react';
import { UploadImage } from '../../static/components/UploadImage';
import {
  clickButton,
  fakeFile,
  fakeRequest,
  fakeUploadRequestParams,
  fillLocation,
  fillTags,
  fillText,
  inputFile,
  triggerRequestSuccess
} from '../utils';
import { ITag } from '../../utils';
import PhotoService from '../../services/PhotoService';
import { renderWithWrapper } from '../utils';

jest.setTimeout(10000);

describe('Test UploadImage', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('tests error when user picks file too large', async () => {
    //GIVEN
    const query = 'par';
    const location = {
      address: 'Paris, France',
      position: { lat: 0.0, lng: 0.0 }
    };
    const spyRequestFunction = fakeRequest({
      '/tag/': { body: '[{"id":"0","name":"tag","version":0}]' },
      [`/geocode?q=${query}&apiKey=${process.env['REACT_APP_GEOCODING_API_KEY']}&lang=fr`]:
        {
          body: JSON.stringify({
            items: [
              {
                address: { label: location.address },
                position: location.position
              }
            ]
          })
        },
      '/user/getSettings': { body: '{"uploadSize":1,"downloadSize":1}' }
    });
    renderWithWrapper(<UploadImage />);
    clickButton(/upload-photo/i);
    const fileInput = screen.getByTestId<HTMLInputElement>('drop-input');
    const files = [fakeFile(100000000, 'image/png')];
    //WHEN
    fillText(/photo.title/, 'Titre');
    fillText(/photoTable.description/, 'Description');
    await fillLocation(query);
    fillTags([{ name: 'tag' }]);
    await act(async () => inputFile(files, fileInput));
    clickButton(/action.add/);
    //THEN
    expect(
      await screen.findByText(/upload.error.overSize/)
    ).toBeInTheDocument();
    expect(spyRequestFunction).not.toBeCalledWith(
      '/photo/upload',
      expect.anything(),
      expect.anything(),
      expect.anything()
    );
  });

  it('tests handling of server error', async () => {
    //GIVEN
    const query = 'par';
    const location = {
      address: 'Paris, France',
      position: { lat: 0.0, lng: 0.0 }
    };
    const serverError = { message: "Une erreur est survenue lors de l'upload" };
    fakeRequest({
      '/user/getSettings': { body: '{"uploadSize":10,"downloadSize":20}' },
      '/tag/': { body: '[{"id":"0","name":"tag","version":0}]' },
      '/photo/upload': { error: JSON.stringify(serverError) },
      [`/geocode?q=${query}&apiKey=${process.env['REACT_APP_GEOCODING_API_KEY']}&lang=fr`]:
        {
          body: JSON.stringify({
            items: [
              {
                address: { label: location.address },
                position: location.position
              }
            ]
          })
        }
    });
    const files = [fakeFile(1000, 'image/png')];
    renderWithWrapper(<UploadImage />);
    clickButton(/upload-photo/i);
    const fileInput = screen.getByTestId<HTMLInputElement>('drop-input');
    //WHEN
    fillText(/photo.title/, 'Titre');
    fillText(/photoTable.description/, 'Description');
    await fillLocation(query);
    fillTags([{ name: 'tag' }]);
    await act(async () => inputFile(files, fileInput));
    clickButton(/action.add/);
    //THEN
    expect(
      await screen.findByText(
        new RegExp(/Une erreur est survenue lors de l'upload/)
      )
    ).toBeInTheDocument();
  });

  it('tests tag is required', async () => {
    //GIVEN
    const query = 'par';
    const location = {
      address: 'Paris, France',
      position: { lat: 0.0, lng: 0.0 }
    };
    const spyRequestFunction = fakeRequest({
      '/tag/': { body: '[{"id":"0","name":"tag","version":0}]' },
      [`/geocode?q=${query}&apiKey=${process.env['REACT_APP_GEOCODING_API_KEY']}&lang=fr`]:
        {
          body: JSON.stringify({
            items: [
              {
                address: { label: location.address },
                position: location.position
              }
            ]
          })
        },
      '/user/getSettings': { body: '{"uploadSize":10,"downloadSize":20}' }
    });
    renderWithWrapper(<UploadImage />);
    clickButton(/upload-photo/i);
    const fileInput = screen.getByTestId<HTMLInputElement>('drop-input');
    const files = [fakeFile(1000, 'image/png')];
    const title = 'Titre';
    const description = 'Description';

    //WHEN
    fillText(/photo.title/, title);
    fillText(/photoTable.description/, description);
    await fillLocation(query);
    await act(async () => inputFile(files, fileInput));
    clickButton(/action.add/);
    //THEN
    expect(spyRequestFunction).not.toBeCalledWith(
      '/photo/upload',
      expect.anything(),
      expect.anything(),
      expect.anything()
    );
  });

  it('tests successful multiupload', async () => {
    //GIVEN
    jest.setTimeout(10000);
    const files = [
      fakeFile(1000, 'image/png', '1.png'),
      fakeFile(1000, 'image/png', '2.png')
    ];
    const title = 'Titre';
    const description = 'Description';
    const tags = [{ name: 'tag' }] as ITag[];
    const query = 'par';
    const location = {
      address: 'Paris, France',
      position: { lat: 0.0, lng: 0.0 }
    };
    const spyRequestFunction = fakeRequest({
      '/tag/': { body: '[{"id":"0","name":"tag","version":0}]' },
      '/photo/upload': { body: "{ message: 'message' }" },
      [`/geocode?q=${query}&apiKey=${process.env['REACT_APP_GEOCODING_API_KEY']}&lang=fr`]:
        {
          body: JSON.stringify({
            items: [
              {
                address: { label: location.address },
                position: location.position
              }
            ]
          })
        },
      '/user/getSettings': { body: '{"uploadSize":10,"downloadSize":20}' }
    });
    const requestParams = [
      fakeUploadRequestParams(files[0], title, description, new Date(), tags),
      fakeUploadRequestParams(files[1], title, description, new Date(), tags)
    ];
    renderWithWrapper(<UploadImage />);
    clickButton(/upload-photo/i);
    const fileInput = screen.getByTestId<HTMLInputElement>('drop-input');
    //WHEN
    fillText(/photo.title/, title);
    fillText(/photoTable.description/, description);
    await fillLocation(query);
    fillTags(tags);
    await act(async () => inputFile(files, fileInput));
    clickButton(/action.add/);
    //THEN
    expect(spyRequestFunction).toBeCalledWith(
      requestParams[0].URL,
      expect.objectContaining(requestParams[0].requestOptions),
      expect.anything(),
      expect.anything()
    );
    expect(spyRequestFunction).toBeCalledWith(
      requestParams[1].URL,
      expect.objectContaining(requestParams[1].requestOptions),
      expect.anything(),
      expect.anything()
    );
    expect(await screen.findByText(/upload.manyUploads/)).toBeVisible();
    expect((await screen.findAllByTestId('DoneIcon')).length).toBe(2);
  });

  it('tests bad multiupload', async () => {
    //GIVEN
    jest.setTimeout(10000);
    jest.spyOn(PhotoService, 'uploadImage');
    const files = [
      fakeFile(1000, 'image/png', '1.png'),
      fakeFile(1000, 'application/zip', '2.png')
    ];
    const title = 'Titre';
    const description = 'Description';
    const tags = [{ name: 'tag' }];
    const query = 'par';
    const location = {
      address: 'Paris, France',
      position: { lat: 0.0, lng: 0.0 }
    };
    fakeRequest({
      '/tag/': { body: '[{"id":"0","name":"tag","version":0}]' },
      '/photo/upload': { body: "{ message: 'message' }" },
      [`/geocode?q=${query}&apiKey=${process.env['REACT_APP_GEOCODING_API_KEY']}&lang=fr`]:
        {
          body: JSON.stringify({
            items: [
              {
                address: { label: location.address },
                position: location.position
              }
            ]
          })
        },
      '/user/getSettings': { body: '{"uploadSize":10,"downloadSize":20}' }
    });
    renderWithWrapper(<UploadImage />);
    clickButton(/upload-photo/i);
    const fileInput = screen.getByTestId<HTMLInputElement>('drop-input');
    //WHEN
    fillText(/photo.title/, title);
    fillText(/photoTable.description/, description);
    await fillLocation(query);
    fillTags(tags);
    await act(async () => inputFile(files, fileInput));
    clickButton(/action.add/);
    //THEN
    expect(PhotoService.uploadImage).toBeCalledWith(
      title,
      description,
      expect.anything(),
      files[0],
      location,
      expect.anything(),
      expect.anything(),
      expect.anything(),
      expect.anything()
    );
    expect(PhotoService.uploadImage).not.toBeCalledWith(
      title,
      description,
      expect.anything(),
      files[1],
      location,
      expect.anything(),
      expect.anything(),
      expect.anything()
    );
  });
});
