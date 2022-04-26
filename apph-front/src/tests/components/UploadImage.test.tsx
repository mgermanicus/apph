import { render } from '@testing-library/react';
import { screen } from '@testing-library/dom';
import UploadImageContainer from '../../static/containers/UploadImageContainer';
import {
  clickButton,
  fakeFile,
  fillText,
  inputFile,
  spyRequest
} from '../utils/library';

describe('Test UploadImage', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('tests error when user picks file too large', () => {
    //GIVEN
    render(<UploadImageContainer />);
    const fileInput = screen.getByTestId<HTMLInputElement>('file-input');
    const file = fakeFile(1000000000, 'image/png');
    const spyRequestFunction = spyRequest();
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
    render(<UploadImageContainer />);
    const fileInput = screen.getByTestId<HTMLInputElement>('file-input');
    const file = fakeFile(1000, 'application/zip');
    const spyRequestFunction = spyRequest();
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

  // TODO test image upload success
  // TODO test error when submitting incomplete form
});
