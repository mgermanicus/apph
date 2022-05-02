import { render, screen } from '@testing-library/react';
import { Header } from '../../static/components/Header';
import { wrapper } from '../utils/components/CustomWrapper';

describe('Header Component Tests', () => {
  it('render when authorized', () => {
    //WHEN
    render(<Header />, { wrapper });
    //THEN
    const link: HTMLAnchorElement = screen.getByRole('link');
    expect(link.getAttribute('href')).toEqual('/me');
  });
});
