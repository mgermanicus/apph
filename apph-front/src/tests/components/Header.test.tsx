import { render, screen } from '@testing-library/react';
import { Header } from '../../static/components/Header';
import { wrapper } from '../utils/components/CustomWrapper';

jest.mock('react-i18next', () => ({
  // this mock makes sure any components using the translate hook can use it without a warning being shown
  useTranslation: () => {
    return {
      t: (str: string) => str,

      i18n: {
        changeLanguage: jest.fn()
      }
    };
  }
}));

describe('Header Component Tests', () => {
  it('render when authorized', () => {
    //WHEN
    render(<Header />, { wrapper });
    //THEN
    const link: HTMLAnchorElement = screen.getByRole('link');
    expect(link.getAttribute('href')).toEqual('/me');
  });
});
