import {
  clickButton,
  fakeFile,
  fillText,
  inputFile,
  spyRequest,
  triggerRequestSuccess
} from '../utils/library';
import { render } from '@testing-library/react';
import UploadImageContainer from '../../static/containers/UploadImageContainer';
import { screen } from '@testing-library/dom';
import Table from '../../static/components/Table';
import * as React from 'react';
import Cookies from 'universal-cookie';
import renderer from 'react-test-renderer';

describe('Tests du composant Table.tsx', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });
  it('Tests array display when data are send', () => {
    //GIVEN
    triggerRequestSuccess(
      '[{"title": "photo1","description": "photo test 1","creationDate": "2022-04-29T12:50:27.973+00:00","shootingDate": "2022-04-29T12:50:27.973+00:00","size": 1200,"tags": "img","url": "fakes url"},{"title": "photo2","description": "photo test 2","creationDate": "1970-01-02T11:58:58.983+00:00","shootingDate": "2022-04-29T12:50:27.973+00:00","tags": "img2","url": "fake url"}] '
    );
    render(<Table />);

    expect(screen.getByText(/photo test 1/)).toBeInTheDocument();
    //WHEN

    //THEN
  });
});
