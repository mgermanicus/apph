import { fireEvent, render } from '@testing-library/react';
import { GlobalSearchBar } from '../../static/components/GlobalSearchBar';
import { screen } from '@testing-library/dom';
import { ITable, ITag } from '../../utils';

const mockedUseNavigate = jest.fn();
jest.mock('react-router-dom', () => ({
  ...jest.requireActual('react-router-dom'),
  useNavigate: () => mockedUseNavigate,
  useLocation: jest.fn().mockImplementation(() => {
    return { pathname: '/search/global/' };
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

describe('Global search bar test', () => {
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

  it('test text shown correctly', () => {
    //WHEN
    render(<GlobalSearchBar />);
    //THEN
    expect(screen.getByLabelText(/field.search/)).toBeInTheDocument();
  });

  it('test text input and key press', async () => {
    //GIVEN
    const utils = render(<GlobalSearchBar />);
    const textInput = utils.getByRole(/textbox/);
    fireEvent.change(textInput, { target: { value: 'test' } });
    //WHEN
    fireEvent.keyPress(textInput, {
      key: 'Enter',
      charCode: 13
    });
    //THEN
    expect(screen.getByDisplayValue(/test/)).toBeInTheDocument();
  });
});
