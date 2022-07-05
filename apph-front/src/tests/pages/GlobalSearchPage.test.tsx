import { render } from '@testing-library/react';
import { ITable, ITag } from '../../utils';
import { GlobalSearchPage } from '../../static/pages/GlobalSearchPage';
import { screen } from '@testing-library/dom';
import {
  clickButton,
  fakeSearchRequestParams,
  spyRequestSuccessBody,
  triggerRequestFailure,
  triggerRequestSuccess
} from '../utils';

jest.mock('react-router-dom', () => ({
  ...jest.requireActual('react-router-dom'),
  useLocation: jest.fn().mockImplementation(() => {
    return { pathname: '/research/global/test' };
  })
}));

jest.mock('react-i18next', () => ({
  // this mock makes sure any components using the translate hook can use it without a warning being shown
  useTranslation: () => {
    return {
      t: (str: string) => str
    };
  }
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
    render(<GlobalSearchPage />);
    //THEN
    expect(screen.getByText(/photo.error.notFound/)).toBeInTheDocument();
  });

  it('test error handling', async () => {
    //GIVEN
    const serverError = '{ "message": "Une erreur" }';
    triggerRequestFailure(serverError);
    //WHEN
    render(<GlobalSearchPage />);
    //THEN
    expect(screen.getByText('Une erreur')).toBeInTheDocument();
  });

  it('test photo display', async () => {
    //GiVEN
    const photoList = JSON.stringify([mockPhoto, mockPhoto]);
    triggerRequestSuccess(`{"photoList":[${photoList}],"total":2}`);
    const spyRequestFunction = spyRequestSuccessBody(
      `{"photoList":[${JSON.stringify(mockPhoto)}],"total":2}`
    );
    const requestParams = fakeSearchRequestParams('test', 1, 1);
    //WHEN
    render(<GlobalSearchPage pageSize={1} />);
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
