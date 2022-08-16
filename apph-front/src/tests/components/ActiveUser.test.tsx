import { ActiveUser } from '../../static/components/ActiveUser';
import {
  renderWithWrapper,
  triggerRequestFailure,
  triggerRequestSuccess
} from '../utils';
import { screen } from '@testing-library/dom';

jest.mock('react-i18next', () => ({
  // this mock makes sure any components using the translate hook can use it without a warning being shown
  useTranslation: () => {
    return {
      t: (str: string) => str
    };
  }
}));

describe('Tests ActiveUesr component', () => {
  it('should render with success message', () => {
    //GIVEN
    triggerRequestSuccess('{"message": "user.redirectionToLogin3s"}');
    //WHEN
    renderWithWrapper(<ActiveUser />);
    //THEN
    expect(screen.getByText(/user.redirectionToLogin3s/)).toBeInTheDocument();
  });

  it('should render with error message', () => {
    //GIVEN
    triggerRequestFailure('{"message": "user.activeError"}');
    //WHEN
    renderWithWrapper(<ActiveUser />);
    //THEN
    expect(screen.getByText(/user.activeError/)).toBeInTheDocument();
  });
});
