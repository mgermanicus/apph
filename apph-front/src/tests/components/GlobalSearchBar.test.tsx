import { fireEvent, render, waitFor } from '@testing-library/react';
import { GlobalSearchBar } from '../../static/components/GlobalSearchBar';
import { screen } from '@testing-library/dom';
import {
  fakeFuzzySearchRequestParams,
  triggerRequestFailure,
  triggerRequestSuccess
} from '../utils';
import { ITable, ITag } from '../../utils';
import userEvent from '@testing-library/user-event';

const mockedUseNavigate = jest.fn();
jest.mock('react-router-dom', () => ({
  ...jest.requireActual('react-router-dom'),
  useNavigate: () => mockedUseNavigate,
  useLocation: jest.fn().mockImplementation(() => {
    return { pathname: '/search/global/' };
  })
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

  it('test text shown correctly', async () => {
    //WHEN
    render(<GlobalSearchBar />);
    //THEN
    expect(screen.getByLabelText(/field.Search/)).toBeInTheDocument();
  });

  it('test suggested word shown correctly', async () => {
    //GIVEN
    const photoList = JSON.stringify([mockPhoto]);
    triggerRequestSuccess(`{"photoList":[${photoList}]}`);
    const spyRequestFunction = triggerRequestSuccess(
      `{"photoList":[${JSON.stringify(mockPhoto)}]}`
    );
    const requestParams = fakeFuzzySearchRequestParams('test');
    render(<GlobalSearchBar />);
    //WHEN
    const input = screen.getByRole('combobox');
    await userEvent.type(input, 'tes');
    await waitFor(() => expect(input).toHaveValue('tes'));
    fireEvent.click(screen.getByRole('button', { name: /testtitle/ }));
    //THEN
    expect(screen.getByRole('combobox')).toHaveValue('testtitle');
    expect(spyRequestFunction).toBeCalledWith(
      requestParams.URL,
      expect.anything(),
      expect.anything(),
      expect.anything()
    );
    expect(mockedUseNavigate).toBeCalled();
  });

  it('test error handling', async () => {
    //GIVEN
    const serverError = '{ "message": "Une erreur" }';
    triggerRequestFailure(serverError);
    render(<GlobalSearchBar />);
    //WHEN
    const input = screen.getByRole('combobox');
    await userEvent.type(input, 'tes');
    await waitFor(() => expect(input).toHaveValue('tes'));
    //THEN
    expect(screen.getByText('Une erreur')).toBeInTheDocument();
  });

  it('test text input and key press', async () => {
    //GIVEN
    const utils = render(<GlobalSearchBar />);
    const textInput = utils.getByRole(/combobox/);
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
