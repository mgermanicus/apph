import { render } from '@testing-library/react';
import { screen } from '@testing-library/dom';
import UploadImageContainer from '../../static/containers/UploadImageContainer';
import { bigImage, inputFile } from '../utils/libraryUpload';
import { clickButton, fillText } from '../utils/library';

describe('Test UploadImage', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('tests error when user chooses file too large', () => {
    //GIVEN
    render(<UploadImageContainer />);
    const fileInput = screen.getByTestId<HTMLInputElement>('file-input');
    const file = bigImage(1000000000);
    //WHEN
    fillText(/Titre de la photo/, 'Titre');
    inputFile(file, fileInput);
    clickButton(/Ajouter/);
    //THEN
    expect(
      screen.getByText(/La taille du fichier excède la limite maximale/)
    ).toBeInTheDocument();
  });
});
