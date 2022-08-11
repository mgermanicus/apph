import { DeleteUser } from '../../static/components/DeleteUser';
import { screen, fireEvent, render } from '@testing-library/react';
import { clickButton, triggerRequestSuccess } from '../utils';

describe('Test DeleteUser', () => {
  it('test delete', () => {
    //GIVEN
    triggerRequestSuccess('');
    const email = 'test@viseo.com';
    const setRefresh = jest.fn();
    render(<DeleteUser email={email} setRefresh={setRefresh} />);
    //WHEN
    fireEvent.click(screen.getByTestId('DeleteIcon'));
    clickButton(/action\.continue/);
    //THEN
    expect(setRefresh).toBeCalled();
  });
});
