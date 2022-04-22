import { render, screen } from '@testing-library/react';
import { Header } from '../../static/components/Header';
import { MemoryRouter } from 'react-router-dom';

describe('Header Component Tests', () => {
  it('render when not authorized', () => {
    //WHEN
    render(<Header isAuth={false} />);
    //THEN
    screen.getByTestId('LoginIcon');
  });

  it('render when authorized', () => {
    //WHEN
    render(<Header isAuth={true} />, { wrapper: MemoryRouter });
    //THEN
    const link: HTMLAnchorElement = screen.getByRole('link');
    expect(link.getAttribute('href')).toEqual('/me');
  });
});
