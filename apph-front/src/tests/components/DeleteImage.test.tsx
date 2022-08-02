import { render, waitFor } from '@testing-library/react';

import {
  clickButton,
  fakeDeleteRequestParams,
  triggerRequestFailure,
  triggerRequestSuccess
} from '../utils';
import { screen } from '@testing-library/dom';
import { DeleteImage } from '../../static/components/DeleteImage';

describe('Create delete button tests', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('test successful file deleted', async () => {
    //GIVEN
    const ids = [1];
    const setPage = jest.fn();
    const setRefresh = jest.fn();
    render(<DeleteImage ids={ids} setPage={setPage} setRefresh={setRefresh} />);
    clickButton(/delete-photo/i);
    triggerRequestSuccess('{ "message": "Suppression effectuée avec succès" }');
    const spyRequestFunction = triggerRequestSuccess(
      '{ "message": "Suppression effectuée avec succès" }'
    );
    const requestParams = fakeDeleteRequestParams(ids);
    //WHEN
    clickButton(/action.continue/);
    //THEN
    expect(spyRequestFunction).toBeCalledWith(
      requestParams.URL,
      expect.anything(),
      expect.anything(),
      expect.anything()
    );
    await waitFor(() => expect(setPage).toBeCalledWith(0));
    await waitFor(() => expect(setRefresh).toBeCalled());
  });

  it('test error handling', () => {
    //GIVEN
    const ids = [100];
    render(<DeleteImage ids={ids} />);
    clickButton(/delete-photo/i);
    const serverError =
      '{ "message": "Une erreur est survenue lors de la suppresion" }';
    triggerRequestFailure(serverError);
    //WHEN
    clickButton(/action.continue/);
    //THEN
    expect(
      screen.getByText('Une erreur est survenue lors de la suppresion')
    ).toBeInTheDocument();
  });

  it('test no file selected', () => {
    //GIVEN
    render(<DeleteImage ids={[]} />);
    //WHEN
    clickButton(/delete-photo/i);
    //THEN
    expect(screen.getByText('photo.noneSelected')).toBeInTheDocument();
  });
});
