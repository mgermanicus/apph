import { fireEvent, getByText, render, screen } from '@testing-library/react';
import {
  clickButton,
  fillPassword,
  fillText,
  triggerRequestFailure,
  triggerRequestSuccess
} from '../utils';
import { PasswordReset } from '../../static/components/PasswordReset';
import { wrapper } from '../utils/components/CustomWrapper';
jest.mock('react-i18next', () => ({
  // this mock makes sure any components using the translate hook can use it without a warning being shown
  useTranslation: () => {
    return {
      t: (str: string) => str,
      i18n: {
        language: 'fr'
      }
    };
  }
}));

describe('Test du composant PasswordReset.tsx', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('resetting password ', () => {
    triggerRequestSuccess('');
    render(<PasswordReset />, { wrapper });
    const newPasswordInput = screen
      .getByTestId(/newPassword/)
      .querySelector('input');
    const confirmPasswordInput = screen
      .getByTestId(/confirmPassword/)
      .querySelector('input');
    const submit = screen.getByText(/action.confirm/);
    fireEvent.change(newPasswordInput as Element, {
      target: { value: 'P@ssw0rd' }
    });
    fireEvent.change(confirmPasswordInput as Element, {
      target: { value: 'P@ssw0rd' }
    });
    fireEvent.click(submit, { button: 0 });
    expect(screen.getByText(/user.passwordChanged/)).toBeInTheDocument();
  });
  it('resetting password with not same password and password confirmation', () => {
    triggerRequestSuccess('');
    render(<PasswordReset />, { wrapper });
    const newPasswordInput = screen
      .getByTestId(/newPassword/)
      .querySelector('input');
    const confirmPasswordInput = screen
      .getByTestId(/confirmPassword/)
      .querySelector('input');
    const submit = screen.getByText(/action.confirm/);
    fireEvent.change(newPasswordInput as Element, {
      target: { value: 'P@ssw0rd' }
    });
    fireEvent.change(confirmPasswordInput as Element, {
      target: { value: 'BadP@ssw0rd' }
    });
    fireEvent.click(submit, { button: 0 });
    expect(screen.getByText(/user.error.passwordNotMatch/)).toBeInTheDocument();
  });
});
