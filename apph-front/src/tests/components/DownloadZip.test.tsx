import { act, render } from '@testing-library/react';

import {
  clickButton,
  fakeDownloadZipFolderRequestParams,
  fakeDownloadZipRequestParams,
  triggerRequestFailure,
  triggerRequestSuccess
} from '../utils';
import { screen } from '@testing-library/dom';
import { DownloadZip } from '../../static/components/DownloadZip';

describe('Create download zip button tests', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('test successful file downloaded', async () => {
    //GIVEN
    const ids = [1];
    render(<DownloadZip ids={ids} />);
    triggerRequestSuccess(
      '{"title":"photos","extension":".zip","data":"test"}'
    );
    const spyRequestFunction = triggerRequestSuccess(
      '{"title":"photos","extension":".zip","data":"test"}'
    );
    const requestParams = fakeDownloadZipRequestParams(ids);
    //WHEN
    await act(async () => {
      await clickButton(/download-zip/i);
    });
    //THEN
    expect(spyRequestFunction).toBeCalledWith(
      requestParams.URL,
      expect.anything(),
      expect.anything(),
      expect.anything()
    );
  });

  it('test error handling', async () => {
    //GIVEN
    const ids = [100];
    render(<DownloadZip ids={ids} />);
    const serverError =
      '{ "message": "Une erreur est survenue lors du téléchargement" }';
    triggerRequestFailure(serverError);
    //WHEN
    await act(async () => {
      await clickButton(/download-zip/i);
    });
    //THEN
    expect(
      screen.getByText('Une erreur est survenue lors du téléchargement')
    ).toBeInTheDocument();
  });

  it('test no file selected', () => {
    //GIVEN
    render(<DownloadZip ids={[]} />);
    //WHEN
    clickButton(/download-zip/i);
    //THEN
    expect(screen.getByText('photo.noneSelected')).toBeInTheDocument();
  });

  it('test successful folder downloaded', async () => {
    //GIVEN
    const ids = [1];
    render(<DownloadZip ids={ids} isFolder={true} />);
    triggerRequestSuccess('{"title":"folder","data":"test"}');
    const spyRequestFunction = triggerRequestSuccess(
      '{"title":"folder","data":"test"}'
    );
    const requestParams = fakeDownloadZipFolderRequestParams(ids[0]);
    //WHEN
    await act(async () => {
      await clickButton(/download-zip/i);
    });
    //THEN
    expect(spyRequestFunction).toBeCalledWith(
      requestParams.URL,
      expect.anything(),
      expect.anything(),
      expect.anything()
    );
  });

  it('test error handling folder', async () => {
    //GIVEN
    const ids = [100];
    render(<DownloadZip ids={ids} isFolder={true} />);
    const serverError =
      '{ "message": "Une erreur est survenue lors du téléchargement" }';
    triggerRequestFailure(serverError);
    //WHEN
    await act(async () => {
      await clickButton(/download-zip/i);
    });
    //THEN
    expect(
      screen.getByText('Une erreur est survenue lors du téléchargement')
    ).toBeInTheDocument();
  });
});
