import { render } from '@testing-library/react';
import { screen } from '@testing-library/dom';
import UploadImageContainer from '../../static/containers/UploadImageContainer';
import { bigImage, uploadFile } from '../utils/libraryUpload';
import { clickButton } from '../utils/library';

describe('Test UploadImage', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('tests when user chooses file too large', () => {
    //GIVEN
    render(<UploadImageContainer />);
    const fileInput = screen.getByTestId<HTMLInputElement>('file-input');
    const file = bigImage(1000000000);
    //WHEN
    uploadFile(file, fileInput);
    clickButton(/Ajouter/);
    //THEN
    screen.debug();
    expect(
      screen.getByText(/La taille du fichier excède la limite maximale/)
    ).toBeInTheDocument();
  });
});
