import {
  clickButton,
  fakeFile,
  fakeRequest,
  inputFile,
  JWS_TOKEN,
  triggerRequestSuccess
} from '../utils';
import Cookies from 'universal-cookie';
import jwtDecode from 'jwt-decode';
import { render, screen } from '@testing-library/react';
import { ReUploadPhoto } from '../../static/components/ReUploadPhoto';

describe('Test ReUploadPhoto', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('render re-upload photo with success', () => {
    //GIVEN
    triggerRequestSuccess('success');
    const cookies = new Cookies();
    const decodedToken = jwtDecode(JWS_TOKEN);
    if (decodedToken !== null && typeof decodedToken === 'object') {
      cookies.set('user', { ...decodedToken, token: JWS_TOKEN });
    }
    const files = [fakeFile(1000, 'image/png', '1.png')];
    triggerRequestSuccess('{"uploadSize":1,"downloadSize":1}');
    render(
      <ReUploadPhoto
        photoId={0}
        updateData={() => {
          return;
        }}
      />
    );
    clickButton(/re-upload-photo/i);
    const fileInput = screen.getByTestId<HTMLInputElement>('file-input');
    inputFile(files, fileInput);
    //WHEN
    clickButton(/action.confirm/);
    //THEN
    expect(screen.getByText(/upload.successChange/)).toBeInTheDocument();
  });

  it('render re-upload photo without file', () => {
    //GIVEN
    fakeRequest({
      '/user/getSettings': { body: '{"uploadSize":10,"downloadSize":20}' },
      '/photo/reupload': {
        body: JSON.stringify({ message: 'success' })
      }
    });
    const cookies = new Cookies();
    const decodedToken = jwtDecode(JWS_TOKEN);
    if (decodedToken !== null && typeof decodedToken === 'object') {
      cookies.set('user', { ...decodedToken, token: JWS_TOKEN });
    }
    render(
      <ReUploadPhoto
        photoId={0}
        updateData={() => {
          return;
        }}
      />
    );
    clickButton(/re-upload-photo/i);
    //WHEN
    clickButton(/action.confirm/);
    //THEN
    expect(screen.getByText(/upload.maySelected/)).toBeInTheDocument();
  });

  it('render re-upload photo with server erreur', () => {
    //GIVEN
    fakeRequest({
      '/user/getSettings': { body: '{"uploadSize":10,"downloadSize":20}' },
      '/photo/reupload': {
        error: JSON.stringify({ message: "Le fichier n'existe pas" })
      }
    });
    const cookies = new Cookies();
    const decodedToken = jwtDecode(JWS_TOKEN);
    if (decodedToken !== null && typeof decodedToken === 'object') {
      cookies.set('user', { ...decodedToken, token: JWS_TOKEN });
    }
    const files = [fakeFile(1000, 'image/png', '1.png')];
    render(
      <ReUploadPhoto
        photoId={0}
        updateData={() => {
          return;
        }}
      />
    );
    clickButton(/re-upload-photo/i);
    const fileInput = screen.getByTestId<HTMLInputElement>('file-input');
    inputFile(files, fileInput);
    //WHEN
    clickButton(/action.confirm/);
    //THEN
    expect(screen.getByText(/Le fichier n'existe pas/)).toBeInTheDocument();
  });
});
