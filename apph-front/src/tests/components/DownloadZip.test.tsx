import { act, render } from '@testing-library/react';

import {
  clickButton,
  fakeDownloadZipRequestParams,
  spyRequestSuccessBody,
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
    render(<DownloadZip ids={ids} titleZip={'test'} />);
    triggerRequestSuccess(
      '{"title":"photos","extension":".zip","data":"test"}'
    );
    const spyRequestFunction = spyRequestSuccessBody(
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
    render(<DownloadZip ids={ids} titleZip={'test'} />);
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
    render(<DownloadZip ids={[]} titleZip={'test'} />);
    //WHEN
    clickButton(/download-zip/i);
    //THEN
    expect(screen.getByText('Aucune photo sélectionnée')).toBeInTheDocument();
  });
});
