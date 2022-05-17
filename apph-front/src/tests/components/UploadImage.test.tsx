import { render } from '@testing-library/react';
import { screen } from '@testing-library/dom';
import { UploadImage } from '../../static/components/UploadImage';
import {
  clickButton,
  fakeFile,
  fakeUploadRequestParams,
  fillText,
  inputFile,
  spyRequestFailure,
  spyRequestSuccess,
  triggerRequestFailure
} from '../utils';
import { ITag } from '../../utils';

describe('Test UploadImage', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('tests error when user picks file too large', () => {
    //GIVEN
    render(<UploadImage />);
    clickButton(/Upload/);
    const fileInput = screen.getByTestId<HTMLInputElement>('file-input');
    const file = fakeFile(1000000000, 'image/png');
    const spyRequestFunction = spyRequestFailure('');
    //WHEN
    fillText(/Titre de la photo/, 'Titre');
    inputFile(file, fileInput);
    clickButton(/Ajouter/);
    //THEN
    expect(
      screen.getByText(/La taille du fichier excède la limite maximale/)
    ).toBeInTheDocument();
    expect(spyRequestFunction).not.toBeCalled();
  });

  it('tests error when user picks invalid file format', () => {
    //GIVEN
    render(<UploadImage />);
    clickButton(/Upload/);
    const fileInput = screen.getByTestId<HTMLInputElement>('file-input');
    const file = fakeFile(1000, 'application/zip');
    const spyRequestFunction = spyRequestFailure('');
    //WHEN
    fillText(/Titre de la photo/, 'Titre');
    inputFile(file, fileInput);
    clickButton(/Ajouter/);
    //THEN
    expect(
      screen.getByText(/Le format du fichier n'est pas valide/)
    ).toBeInTheDocument();
    expect(spyRequestFunction).not.toBeCalled();
  });

  it('tests successful file upload', () => {
    //GIVEN
    render(<UploadImage />);
    clickButton(/Upload/);
    const fileInput = screen.getByTestId<HTMLInputElement>('file-input');
    const file = fakeFile(1000, 'image/png');
    const title = 'Titre';
    const description = 'Description';
    const tags = [{ name: 'tag' }] as ITag[];
    const spyRequestFunction = spyRequestSuccess();
    const requestParams = fakeUploadRequestParams(
      file,
      title,
      description,
      new Date(),
      tags
    );
    //WHEN
    fillText(/Titre de la photo/, title);
    inputFile(file, fileInput);
    clickButton(/Ajouter/);
    //THEN
    expect(spyRequestFunction).toBeCalledWith(
      requestParams.URL,
      expect.objectContaining(requestParams.requestOptions),
      expect.anything(),
      expect.anything()
    );
  });

  it('tests handling of server error', () => {
    //GIVEN
    render(<UploadImage />);
    clickButton(/Upload/);
    const serverError =
      '{"message":"Une erreur est survenue lors de l\'upload"}';
    const fileInput = screen.getByTestId<HTMLInputElement>('file-input');
    const file = fakeFile(1000, 'image/png');
    triggerRequestFailure(serverError);
    //WHEN
    fillText(/Titre de la photo/, 'Titre');
    inputFile(file, fileInput);
    clickButton(/Ajouter/);
    //THEN
    expect(
      screen.getByText(new RegExp("Une erreur est survenue lors de l'upload"))
    ).toBeInTheDocument();
  });
});
