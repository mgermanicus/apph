import { fireEvent, render } from '@testing-library/react';
import { GlobalSearchBar } from '../../static/components/GlobalSearchBar';
import { screen } from '@testing-library/dom';
import { fillSearch } from '../utils';

const mockedUsedNavigate = jest.fn();
jest.mock('react-router-dom', () => ({
  ...jest.requireActual('react-router-dom'),
  useNavigate: () => mockedUsedNavigate,
  useLocation: jest.fn().mockImplementation(() => {
    return { pathname: '/search/global/test' };
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
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('test text shown correctly', async () => {
    //WHEN
    render(<GlobalSearchBar />);
    //THEN
    expect(screen.getByPlaceholderText(/field.Search/)).toBeInTheDocument();
  });

  it('test text input and key press', async () => {
    //WHEN
    render(<GlobalSearchBar />);
    fillSearch(/field.Search/, 'test');
    fireEvent.keyPress(screen.getByDisplayValue(/test/), {
      key: 'Enter',
      charCode: 13
    });
    //THEN
    expect(screen.getByDisplayValue(/test/)).toBeInTheDocument();
  });
});
