import { act, render } from '@testing-library/react';
import { screen } from '@testing-library/dom';
import { UploadImage } from '../../static/components/UploadImage';
import {
  clickButton,
  fakeFile,
  fakeRequest,
  fakeUploadRequestParams,
  fillDate,
  fillTags,
  fillText,
  inputFile,
  spyRequestSuccessBody
} from '../utils';
import { ITag } from '../../utils';
import { wrapper } from '../utils/components/CustomWrapper';

describe('Test UploadImage', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('tests error when user picks file too large', async () => {
    //GIVEN
    const spyRequestFunction = fakeRequest({
      '/tag/': { body: '[{"id":"0","name":"tag","version":0}]' }
    });
    render(<UploadImage />, { wrapper });
    clickButton(/upload-photo/i);
    const fileInput = screen.getByTestId<HTMLInputElement>('file-input');
    const files = [fakeFile(100000000, 'image/png')];
    //WHEN
    // Must put 'await act' to wait for the component's state to update before submitting
    // otherwise the component does not have enough time and selectedTags is not set when handleSubmit is called
    await act(() => {
      fillText(/Titre de la photo/, 'Titre');
      fillText(/Description/, 'Description');
      fillTags([{ name: 'tag' }]);
      inputFile(files, fileInput);
    });
    clickButton(/Ajouter/);
    //THEN
    expect(
      screen.getByText(/La taille du fichier excède la limite maximale/)
    ).toBeInTheDocument();
    expect(spyRequestFunction).not.toBeCalledWith(
      '/photo/upload',
      expect.anything(),
      expect.anything(),
      expect.anything()
    );
  });

  it('tests error when user picks invalid file format', async () => {
    //GIVEN
    const files = [fakeFile(1000, 'application/zip')];
    const spyRequestFunction = fakeRequest({
      '/tag/': { body: '[{"id":"0","name":"tag","version":0}]' }
    });
    render(<UploadImage />, { wrapper });
    clickButton(/upload-photo/i);
    const fileInput = screen.getByTestId<HTMLInputElement>('file-input');
    //WHEN
    await act(() => {
      fillText(/Titre de la photo/, 'Titre');
      fillText(/Description/, 'Description');
      fillTags([{ name: 'tag' }]);
      inputFile(files, fileInput);
    });
    clickButton(/Ajouter/);
    //THEN
    expect(spyRequestFunction).not.toBeCalledWith(
      '/photo/upload',
      expect.anything(),
      expect.anything(),
      expect.anything()
    );
    expect(
      screen.getByText(/Le format du fichier n'est pas valide/)
    ).toBeInTheDocument();
  });

  it('tests successful file upload', async () => {
    //GIVEN
    const spyRequestFunction = fakeRequest({
      '/tag/': { body: '[{"id":"0","name":"tag","version":0}]' },
      '/photo/upload': { body: 'body' }
    });
    const files = [fakeFile(1000, 'image/png')];
    const title = 'Titre';
    const description = 'Description';
    const date = new Date('1995-12-17T03:24:00');
    const tags = [{ name: 'tag' }] as ITag[];
    const requestParams = fakeUploadRequestParams(
      files[0],
      title,
      description,
      date,
      tags
    );
    act(() => {
      render(<UploadImage />, { wrapper });
    });
    act(() => {
      clickButton(/upload-photo/i);
    });
    const fileInput = screen.getByTestId<HTMLInputElement>('file-input');
    //WHEN
    await act(() => {
      fillText(/Titre de la photo/, title);
      fillText(/Description/, description);
      fillDate(date);
      fillTags(tags);
      inputFile(files, fileInput);
    });
    clickButton(/Ajouter/);
    //THEN
    expect(spyRequestFunction).toBeCalledWith(
      '/photo/upload',
      expect.objectContaining(requestParams.requestOptions),
      expect.anything(),
      expect.anything()
    );
    expect(
      await screen.findByText(/Vos fichiers ont bien été uploadés/)
    ).toBeVisible();
    expect(await screen.findByTestId('DoneIcon')).toBeVisible();
  });

  it('tests handling of server error', async () => {
    //GIVEN
    const serverError =
      '{"message":"Une erreur est survenue lors de l\'upload"}';
    fakeRequest({
      '/tag/': { body: '[{"id":"0","name":"tag","version":0}]' },
      '/photo/upload': { error: serverError }
    });
    const files = [fakeFile(1000, 'image/png')];
    act(() => {
      render(<UploadImage />, { wrapper });
    });
    act(() => {
      clickButton(/upload-photo/i);
    });
    const fileInput = screen.getByTestId<HTMLInputElement>('file-input');
    //WHEN
    await act(() => {
      fillText(/Titre de la photo/, 'Titre');
      fillText(/Description/, 'Description');
      fillTags([{ name: 'tag' }]);
      inputFile(files, fileInput);
    });
    clickButton(/Ajouter/);
    //THEN
    expect(
      await screen.findByText(
        new RegExp("Une erreur est survenue lors de l'upload")
      )
    ).toBeInTheDocument();
  });

  it('tests tag creation', async () => {
    //GIVEN
    const spyRequestFunction = fakeRequest({
      '/tag/': { body: '[]' },
      '/photo/upload': { body: 'body' }
    });
    const files = [fakeFile(1000, 'image/png')];
    render(<UploadImage />, { wrapper });
    clickButton(/upload-photo/i);
    const fileInput = screen.getByTestId<HTMLInputElement>('file-input');
    const file = fakeFile(1000, 'image/png');
    const title = 'Titre';
    const description = 'Description';
    const date = new Date('1995-12-17T03:24:00');
    const tags = [{ name: 'newTag' }] as ITag[];
    const requestParams = fakeUploadRequestParams(
      file,
      title,
      description,
      date,
      [{ name: '+ Add New Tag newTag' }]
    );
    //WHEN
    await act(() => {
      fillText(/Titre de la photo/, title);
      fillText(/Description/, description);
      fillDate(date);
      fillTags(tags);
      inputFile(files, fileInput);
    });
    await act(() => clickButton(/Ajouter/));
    //THEN
    expect(spyRequestFunction).toBeCalledWith(
      '/photo/upload',
      expect.objectContaining(requestParams.requestOptions),
      expect.anything(),
      expect.anything()
    );
  });

  it('tests tag is required', async () => {
    //GIVEN
    const spyRequestFunction = fakeRequest({
      '/tag/': { body: '[{"id":"0","name":"tag","version":0}]' }
    });
    render(<UploadImage />, { wrapper });
    clickButton(/upload-photo/i);
    const fileInput = screen.getByTestId<HTMLInputElement>('file-input');
    const files = [fakeFile(1000, 'image/png')];
    const title = 'Titre';
    const description = 'Description';
    //WHEN
    await act(() => {
      fillText(/Titre de la photo/, title);
      fillText(/Description/, description);
      inputFile(files, fileInput);
    });
    clickButton(/Ajouter/);
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
    act(() => {
      render(<UploadImage />, { wrapper });
    });
    act(() => {
      clickButton(/upload-photo/i);
    });
    const fileInput = screen.getByTestId<HTMLInputElement>('file-input');
    //WHEN
    act(() => {
      fillText(/Titre de la photo/, title);
      fillText(/Description/, description);
      fillTags(tags);
      inputFile(files, fileInput);
    });
    clickButton(/Ajouter/);
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
    expect(
      await screen.findByText(/Vos fichiers ont bien été uploadés/)
    ).toBeVisible();
    expect((await screen.findAllByTestId('DoneIcon')).length).toBe(2);
  });

  it('tests multiupload failure', async () => {
    //GIVEN
    const files = [
      fakeFile(1000, 'image/png', '1.png'),
      fakeFile(1000, 'application/zip', '2.png')
    ];
    const title = 'Titre';
    spyRequestSuccessBody('[{"id":"0","name":"tag","version":0}]');
    act(() => {
      render(<UploadImage />, { wrapper });
    });
    act(() => {
      clickButton(/upload-photo/i);
    });
    const fileInput = screen.getByTestId<HTMLInputElement>('file-input');
    //WHEN
    act(() => {
      fillText(/Titre de la photo/, title);
      fillText(/Description/, description);
      fillTags(tags);
      inputFile(files, fileInput);
    });
    clickButton(/Ajouter/);
    //THEN
    expect(
      await screen.findByText(/Certains fichiers n'ont pas pu être uploadés/)
    ).toBeVisible();
    expect(
      await screen.findByText(/Le format du fichier n'est pas valide/)
    ).toBeVisible();
  });
});
