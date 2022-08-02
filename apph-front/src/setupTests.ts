// jest-dom adds custom jest matchers for asserting on DOM nodes.
// allows you to do things like:
// expect(element).toHaveTextContent(/react/i)
// learn more: https://github.com/testing-library/jest-dom
import '@testing-library/jest-dom';
import { ReactElement } from 'react';
import { wrapper } from './tests/utils/components/CustomWrapper';
import { render } from '@testing-library/react';

jest.mock('react-i18next', () => ({
  // this mock makes sure any components using the translate hook can use it without a warning being shown
  useTranslation: () => {
    return {
      t: (str: string) => str,
      i18n: {
        changeLanguage: jest.fn(),
        language: 'fr'
      }
    };
  }
}));

jest.mock('ts-debounce', () => ({
  debounce: (func: (query: string) => Promise<void>, waitMs: number) => func
}));

const mockedUseNavigate = jest.fn();
const mockedUseLocation = jest.fn();
jest.mock('react-router-dom', () => ({
  ...jest.requireActual('react-router-dom'),
  useNavigate: () => mockedUseNavigate,
  useLocation: () => mockedUseLocation
}));

const renderWithWrapper = (component: ReactElement) =>
  render(component, { wrapper });

export { mockedUseNavigate, mockedUseLocation, renderWithWrapper };
