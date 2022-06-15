import { render, screen } from '@testing-library/react';
import { ErrorCard } from '../../static/components/ErrorCard';

jest.mock('react-i18next', () => ({
  // this mock makes sure any components using the translate hook can use it without a warning being shown
  useTranslation: () => {
    return {
      t: (str: string) => str
    };
  }
}));

describe('ErrorCard Component Tests', () => {
  it('render with message', () => {
    //GIVEN
    const error = 'Error Message';
    //WHEN
    render(<ErrorCard errorMessage={error} />);
    //THEN
    const message: HTMLParagraphElement = screen.getByText('Message: ' + error);
    expect(message.innerHTML).toEqual('Message: ' + error);
  });
});
