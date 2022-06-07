import { render, screen } from '@testing-library/react';
import { clickButton, fillText, selectOptionInListBox } from '../utils';
import { wrapper } from '../utils/components/CustomWrapper';
import { FilterSelector } from '../../static/components/FilterSelector';

/*jest.mock('../../utils/hooks/usePhotoTable.tsx', () => () => ({
  errorState: {
    getMessage: '',
    setMessage: () => null
  },
  photoTable: null
}));*/

describe('Test du fonctionnement des filtres', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('test if a filter is correctly added', () => {
    //GIVEN
    const onError = () => null;
    const onFilterPhoto = () => null;
    render(<FilterSelector onError={onError} onFilterPhoto={onFilterPhoto} />, {
      wrapper
    });
    //WHEN
    clickButton(/Ajouter un filtre/);
    //THEN
    expect(screen.getAllByLabelText(/Champ/).length).toBe(2);
  });

  it('test if a filter is correctly deleted', () => {
    //GIVEN
    const onError = () => null;
    const onFilterPhoto = () => null;
    render(<FilterSelector onError={onError} onFilterPhoto={onFilterPhoto} />, {
      wrapper
    });
    //WHEN
    clickButton(/Supprimer un filtre/);
    //THEN
    expect(screen.queryByText(/Champ/)).not.toBeInTheDocument();
  });

  it('test that filter datas are correctly submitted', () => {
    //GIVEN
    const spyFilterPhoto = jest.fn();
    const spyOpenAlert = jest.fn();
    render(
      <FilterSelector onFilterPhoto={spyFilterPhoto} onError={spyOpenAlert} />,
      { wrapper }
    );
    const fieldInput = screen.getByLabelText(/Champ/);
    const operatorInput = screen.getByLabelText(/Opérateur/);
    //WHEN
    selectOptionInListBox(fieldInput, /Titre/);
    selectOptionInListBox(operatorInput, /EGAL/);
    fillText(/Valeur/, 'Test');
    clickButton(/Rechercher/);
    //THEN
    expect(spyFilterPhoto).toBeCalledWith([
      {
        id: 0,
        field: 'title',
        operator: 'is',
        value: 'Test'
      }
    ]);
  });
});
