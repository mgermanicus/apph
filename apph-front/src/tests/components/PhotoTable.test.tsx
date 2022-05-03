import { triggerRequestSuccess } from '../utils/library';
import { render } from '@testing-library/react';
import { screen } from '@testing-library/dom';
import Table from '../../static/components/PhotoTable';
import * as React from 'react';

describe('Tests du composant PhotoTable.tsx', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });
  it('Tests array display when data are send', () => {
    //GIVEN
    triggerRequestSuccess(
      '[{"title": "photo1","description": "photo test 1","creationDate": "2022-04-29T12:50:27.973+00:00","shootingDate": "2022-04-29T12:50:27.973+00:00","size": 1200,"tags": "img","url": "fakes url"},{"title": "photo2","description": "photo test 2","creationDate": "1970-01-02T11:58:58.983+00:00","shootingDate": "2022-04-29T12:50:27.973+00:00","tags": "img2","url": "fake url"}] '
    );
    //WHEN
    render(<Table />);
    //THEN
    expect(screen.getByText(/photo test 1/)).toBeInTheDocument();
  });
});
