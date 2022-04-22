import { render, screen } from '@testing-library/react';
import { UserAvatar } from '../../static/components/UserAvatar';

describe('UserAvatar Component Tests', () => {
  it('render with message', () => {
    //WHEN
    render(<UserAvatar firstname={'toto'} lastname={'test'} />);
    //THEN
    const avatar: HTMLDivElement = screen.getByText('tt');
    expect(avatar.innerHTML).toEqual('tt');
  });
});
