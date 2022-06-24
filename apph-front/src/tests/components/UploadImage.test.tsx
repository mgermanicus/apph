import { act, render, screen } from '@testing-library/react';
import { UploadImage } from '../../static/components/UploadImage';
import {
  clickButton,
  fakeFile,
  fakeRequest,
  fakeUploadRequestParams,
  fillTags,
  fillText,
  inputFile,
  spyRequestSuccessBody
} from '../utils';
import { ITag } from '../../utils';
import { wrapper } from '../utils/components/CustomWrapper';
import PhotoService from '../../services/PhotoService';

jest.mock('react-i18next', () => ({
  // this mock makes sure any components using the translate hook can use it without a warning being shown
  useTranslation: () => {
    return {
      t: (str: string) => str
    };
  }
}));

describe('Test UploadImage', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('tests error when user picks file too large', async () => {
    //GIVEN
    const spyRequestFunction = fakeRequest({
      '/tag/': { body: '[{"id":"0","name":"tag","version":0}]' },
      '/admin/getSettings': { body: '{"uploadSize":1,"downloadSize":1}' }
    });
    render(<UploadImage />, { wrapper });
    clickButton(/upload-photo/i);
    const fileInput = screen.getByTestId<HTMLInputElement>('drop-input');
    const files = [fakeFile(100000000, 'image/png')];
    //WHEN
    fillText(/photo.title/, 'Titre');
    fillText(/photoTable.description/, 'Description');
    fillTags([{ name: 'tag' }]);
    await act(async () => inputFile(files, fileInput));
    clickButton(/action.add/);
    //THEN
    expect(screen.getByText(/upload.error.overSize/)).toBeInTheDocument();
    expect(spyRequestFunction).not.toBeCalledWith(
      '/photo/upload',
      expect.anything(),
      expect.anything(),
      expect.anything()
    );
  });

  it('tests handling of server error', async () => {
    //GIVEN
    const serverError = { message: "Une erreur est survenue lors de l'upload" };
    fakeRequest({
      '/admin/getSettings': { body: '{"uploadSize":10,"downloadSize":20}' },
      '/tag/': { body: '[{"id":"0","name":"tag","version":0}]' },
      '/photo/upload': { error: JSON.stringify(serverError) }
    });
    const files = [fakeFile(1000, 'image/png')];
    render(<UploadImage />, { wrapper });
    clickButton(/upload-photo/i);
    const fileInput = screen.getByTestId<HTMLInputElement>('drop-input');
    //WHEN
    fillText(/photo.title/, 'Titre');
    fillText(/photoTable.description/, 'Description');
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
    const spyRequestFunction = fakeRequest({
      '/tag/': { body: '[{"id":"0","name":"tag","version":0}]' },
      '/admin/getSettings': { body: '{"uploadSize":10,"downloadSize":20}' }
    });
    render(<UploadImage />, { wrapper });
    clickButton(/upload-photo/i);
    const fileInput = screen.getByTestId<HTMLInputElement>('drop-input');
    const files = [fakeFile(1000, 'image/png')];
    const title = 'Titre';
    const description = 'Description';
    //WHEN
    fillText(/photo.title/, title);
    fillText(/photoTable.description/, description);
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
    const spyRequestFunction = spyRequestSuccessBody(
      '[{"id":"0","name":"tag","version":0}]'
    );
    const requestParams = [
      fakeUploadRequestParams(files[0], title, description, new Date(), tags),
      fakeUploadRequestParams(files[1], title, description, new Date(), tags)
    ];
    render(<UploadImage />, { wrapper });
    clickButton(/upload-photo/i);
    const fileInput = screen.getByTestId<HTMLInputElement>('drop-input');
    //WHEN
    fillText(/photo.title/, title);
    fillText(/photoTable.description/, description);
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
    spyRequestSuccessBody('[{"id":"0","name":"tag","version":0}]');
    render(<UploadImage />, { wrapper });
    clickButton(/upload-photo/i);
    const fileInput = screen.getByTestId<HTMLInputElement>('drop-input');
    //WHEN
    fillText(/photo.title/, title);
    fillText(/photoTable.description/, description);
    fillTags(tags);
    await act(async () => inputFile(files, fileInput));
    clickButton(/action.add/);
    //THEN
    expect(PhotoService.uploadImage).toBeCalledWith(
      title,
      description,
      expect.anything(),
      files[0],
      expect.anything(),
      expect.anything(),
      undefined,
      expect.anything(),
      expect.anything()
    );
  });
});
