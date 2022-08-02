import { screen } from '@testing-library/react';
import { clickButton, fillText, selectOptionInListBox } from '../utils';
import { FilterSelector } from '../../static/components/FilterSelector';
import { renderWithWrapper } from '../../setupTests';

describe('Test du fonctionnement des filtres', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('test if a filter is correctly added', () => {
    //GIVEN
    const onError = () => null;
    const onFilterPhoto = () => null;
    renderWithWrapper(
      <FilterSelector onError={onError} onFilterPhoto={onFilterPhoto} />
    );
    //WHEN
    clickButton(/filter.add/);
    //THEN
    expect(screen.getAllByLabelText(/filter.field/).length).toBe(2);
  });

  it('test if a filter is correctly deleted', () => {
    //GIVEN
    const onError = () => null;
    const onFilterPhoto = () => null;
    renderWithWrapper(
      <FilterSelector onError={onError} onFilterPhoto={onFilterPhoto} />
    );
    //WHEN
    clickButton(/filter.delete/);
    //THEN
    expect(screen.queryByText(/filter.field/)).not.toBeInTheDocument();
  });

  it('test that filter datas are correctly submitted', () => {
    //GIVEN
    const spyFilterPhoto = jest.fn();
    const spyOpenAlert = jest.fn();
    renderWithWrapper(
      <FilterSelector onFilterPhoto={spyFilterPhoto} onError={spyOpenAlert} />
    );
    const fieldInput = screen.getByLabelText(/filter.field/);
    const operatorInput = screen.getByLabelText(/filter.operator/);
    //WHEN
    selectOptionInListBox(fieldInput, /photoTable.title/);
    selectOptionInListBox(operatorInput, /operator.equal/);
    fillText(/filter.value/, 'Test');
    clickButton(/action.search/);
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

  it('should render with tagName', () => {
    const spyFilterPhoto = jest.fn();
    const spyOpenAlert = jest.fn();

    const tree = renderWithWrapper(
      <FilterSelector
        tagName={'Tag_1'}
        onFilterPhoto={spyFilterPhoto}
        onError={spyOpenAlert}
      />
    );
    expect(tree).toMatchSnapshot();
  });
});
