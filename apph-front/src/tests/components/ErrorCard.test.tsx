import { render, screen } from '@testing-library/react';
import { ErrorCard } from '../../static/components/ErrorCard';

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
