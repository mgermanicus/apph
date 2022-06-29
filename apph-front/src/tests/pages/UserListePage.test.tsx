import { render } from '@testing-library/react';
import { UserListPage } from '../../static/pages/UserListPage';

jest.mock('react-i18next', () => ({
  // this mock makes sure any components using the translate hook can use it without a warning being shown
  useTranslation: () => {
    return {
      t: (str: string) => str
    };
  }
}));

describe('UserListPage', () => {
  it('should render', () => {
    //WHEN
    const tree = render(<UserListPage />);
    //THEN
    expect(tree).toMatchSnapshot();
  });
});
