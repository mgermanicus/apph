import { render } from '@testing-library/react';
import { ITable, ITag } from '../../utils';
import { GlobalSearchPage } from '../../static/pages/GlobalSearchPage';

const mockedUsedLocation = jest.fn();
jest.mock('react-router-dom', () => ({
  ...jest.requireActual('react-router-dom'),
  useLocation: () => mockedUsedLocation
}));

jest.mock('react-i18next', () => ({
  // this mock makes sure any components using the translate hook can use it without a warning being shown
  useTranslation: () => {
    return {
      t: (str: string) => str
    };
  }
}));

describe('Photo complex grid test', () => {
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

  it('test text shown correctly', async () => {
    //WHEN
    render(<GlobalSearchPage />);
    //THEN
    //expect(screen.getByText(/testTitle/)).toBeInTheDocument();
  });
});
