import { renderWithWrapper } from '../utils';
import { FilterComponent } from '../../static/components/FilterComponent';

describe('Test FilterComponent', () => {
  it('tests date', () => {
    const state = {
      id: 0,
      field: 'shootingDate',
      operator: 'equal'
    };
    const mockDispatch = jest.fn();
    renderWithWrapper(
      <FilterComponent
        state={state}
        dispatchFilterState={mockDispatch}
        tagList={[]}
      />
    );
  });

  it('tests tags', () => {
    const state = {
      id: 0,
      field: 'tags',
      operator: 'equal'
    };
    const mockDispatch = jest.fn();
    renderWithWrapper(
      <FilterComponent
        state={state}
        dispatchFilterState={mockDispatch}
        tagList={[{ id: 0, name: 'test' }]}
      />
    );
  });
});
