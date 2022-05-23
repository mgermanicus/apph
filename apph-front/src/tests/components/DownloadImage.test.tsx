import { render } from '@testing-library/react';
import { DownloadImage } from '../../static/components/DownloadImage';

import {
  clickButton,
  fakeDownloadRequestParams,
  spyRequestSuccessBody,
  triggerRequestFailure,
  triggerRequestSuccess
} from '../utils';
import { screen } from '@testing-library/dom';

describe('Create download button tests', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('tests successful file download', () => {
    //GIVEN
    const id = 1;
    render(<DownloadImage id={id} />);
    triggerRequestSuccess(
      '{"id":1,"title":"test","extension":"jpg","data":"test"}'
    );
    const spyRequestFunction = spyRequestSuccessBody(
      '{"id":1,"title":"test","extension":"jpg","data":"test"}'
    );
    const requestParams = fakeDownloadRequestParams(id);
    //WHEN
    clickButton(/download-photo/i);
    //THEN
    expect(spyRequestFunction).toBeCalledWith(
      requestParams.URL,
      expect.anything(),
      expect.anything(),
      expect.anything()
    );
  });

  it('tests error handling', () => {
    //GIVEN
    const id = 100;
    render(<DownloadImage id={id} />);
    const serverError =
      '{ "message": "Une erreur est survenue lors du téléchargement" }';
    triggerRequestFailure(serverError);
    //WHEN
    clickButton(/download-photo/i);
    //THEN
    expect(
      screen.getByText('Une erreur est survenue lors du téléchargement')
    ).toBeInTheDocument();
  });
});
