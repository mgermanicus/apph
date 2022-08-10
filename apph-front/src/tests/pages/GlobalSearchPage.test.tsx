import { ITable, ITag } from '../../utils';
import { GlobalSearchPage } from '../../static/pages/GlobalSearchPage';
import { MemoryRouter } from 'react-router-dom';
import { screen } from '@testing-library/dom';
import {
  clickButton,
  fakeSearchRequestParams,
  triggerRequestFailure,
  triggerRequestSuccess
} from '../utils';
import { render } from '@testing-library/react';

jest.mock('react-router-dom', () => ({
  ...jest.requireActual('react-router-dom'),
  useLocation: jest.fn().mockImplementation(() => {
    return { pathname: '/search/global/' };
  })
}));

describe('global search page test', () => {
  const mockPhoto: ITable = {
    id: 0,
    url: 'https://i.pinimg.com/originals/a2/39/b5/a239b5b33d145fcab7e48544b81019da.jpg',
    title: 'testTitle',
    description: 'testDescription',
    creationDate: new Date(),
    modificationDate: new Date(),
    shootingDate: new Date(),
    size: 0,
    format: 'jpeg',
    tags: [{ id: 1, name: 'testTag' }] as ITag[],
    location: { address: 'Paris, France', position: { lat: 0, lng: 0 } }
  };

  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('test text shown correctly when nothing found', async () => {
    //WHEN
    render(
      <MemoryRouter initialEntries={['?params=test&size=10']}>
        <GlobalSearchPage />
      </MemoryRouter>
    );
    //THEN
    expect(screen.getByText(/photo.error.notFound/)).toBeInTheDocument();
  });

  it('test error handling', async () => {
    //GIVEN
    const serverError = '{ "message": "Une erreur" }';
    triggerRequestFailure(serverError);
    //WHEN
    render(
      <MemoryRouter initialEntries={['?params=test&size=10']}>
        <GlobalSearchPage />
      </MemoryRouter>
    );
    //THEN
    expect(screen.getByText('Une erreur')).toBeInTheDocument();
  });

  it('test photo display', async () => {
    //GIVEN
    const photoList = JSON.stringify([mockPhoto, mockPhoto]);
    triggerRequestSuccess(`{"photoList":[${photoList}],"total":2}}`);
    const spyRequestFunction = triggerRequestSuccess(
      `{"photoList":[${JSON.stringify(mockPhoto)}],"total":2}`
    );
    const requestParams = fakeSearchRequestParams('test', 1, 1);
    //WHEN
    render(
      <MemoryRouter initialEntries={['?params=test']}>
        <GlobalSearchPage pageSize={1} />
      </MemoryRouter>
    );
    clickButton(/Go to page 2/);
    //THEN
    expect(spyRequestFunction).toBeCalledWith(
      requestParams.URL,
      expect.anything(),
      expect.anything(),
      expect.anything()
    );
  });
});
