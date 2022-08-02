import { fireEvent, screen } from '@testing-library/react';
import { Header } from '../../static/components/Header';
import { renderWithWrapper } from '../utils';

const mockChangeLanguage = jest.fn();
jest.mock('react-i18next', () => ({
  // this mock makes sure any components using the translate hook can use it without a warning being shown
  useTranslation: () => {
    return {
      t: (str: string) => str,
      i18n: {
        changeLanguage: mockChangeLanguage,
        language: 'fr'
      }
    };
  }
}));

describe('Header Component Tests', () => {
  it('render when authorized', () => {
    //GIVEN
    renderWithWrapper(<Header />);
    //THEN
    const link: HTMLAnchorElement = screen.getByRole('link');
    expect(link.getAttribute('href')).toEqual('/me');
  });

  it('logout', () => {
    //GIVEN
    renderWithWrapper(<Header />);
    //WHEN
    fireEvent.click(screen.getByTestId('LogoutIcon'));
    //THEN
    expect(document.location.pathname).toBe('/');
  });

  it('open drawer menu', () => {
    //GIVEN
    renderWithWrapper(<Header />);
    //WHEN
    fireEvent.click(screen.getByTestId('MenuIcon'));
    //THEN
    expect(screen.getByText('field.photos')).toBeVisible();
  });

  it('change language', () => {
    //GIVEN
    renderWithWrapper(<Header />);
    //WHEN
    fireEvent.click(screen.getByText('en'));
    //THEN
    expect(mockChangeLanguage).toBeCalledWith('en');
  });
});
