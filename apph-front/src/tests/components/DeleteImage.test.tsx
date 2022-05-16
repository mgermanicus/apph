import { render } from '@testing-library/react';

import {
  clickButton,
  fakeDeleteRequestParams,
  spyRequestSuccessBody,
  triggerRequestFailure,
  triggerRequestSuccess
} from '../utils';
import { screen } from '@testing-library/dom';
import { DeleteImage } from '../../static/components/DeleteImage';

describe('Create delete button tests', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('test successful file deleted', () => {
    //GIVEN
    const ids = [1];
    render(<DeleteImage ids={ids} />);
    clickButton(/Supprimer/);
    triggerRequestSuccess('{ "message": "Suppression effectuée avec succès" }');
    const spyRequestFunction = spyRequestSuccessBody(
      '{ "message": "Suppression effectuée avec succès" }'
    );
    const requestParams = fakeDeleteRequestParams(ids);
    //WHEN
    clickButton(/Continuer/);
    //THEN
    expect(spyRequestFunction).toBeCalledWith(
      requestParams.URL,
      expect.anything(),
      expect.anything(),
      expect.anything()
    );
  });

  it('test error handling', () => {
    //GIVEN
    const ids = [100];
    render(<DeleteImage ids={ids} />);
    clickButton(/Supprimer/);
    const serverError =
      '{ "message": "Une erreur est survenue lors du téléchargement" }';
    triggerRequestFailure(serverError);
    //WHEN
    clickButton(/Continuer/);
    //THEN
    expect(
      screen.getByText('Une erreur est survenue lors du téléchargement')
    ).toBeInTheDocument();
  });

  it('test no file selected', () => {
    //GIVEN
    render(<DeleteImage ids={[]} />);
    //WHEN
    clickButton(/Supprimer/);
    //THEN
    expect(screen.getByText('Aucune photo sélectionnée')).toBeInTheDocument();
  });
});
