import { AlertSnackbar } from '../../static/components/AlertSnackbar';
import { render, screen } from '@testing-library/react';
import { clickButton } from '../utils';

describe('Tests AlertSnackbar', () => {
  it('tests snackbar close', async () => {
    //GIVEN
    const message = 'test';
    const onClose = jest.fn();
    render(<AlertSnackbar message={message} onClose={onClose} open />);
    //WHEN
    screen.debug(screen.getByRole('button', { name: 'Close' }));
    clickButton(/Close/);
    //THEN
    expect(onClose).toBeCalled();
  });
});
