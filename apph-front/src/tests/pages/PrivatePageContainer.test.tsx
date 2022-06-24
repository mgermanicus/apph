import { render } from '@testing-library/react';
import { PrivatePageContainer } from '../../static/pages/PrivatePageContainer';
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

describe('PrivatePageContainer', () => {
  it('should render', () => {
    //WHEN
    const tree = render(<PrivatePageContainer element={<></>} />, { wrapper });
    //THEN
    expect(tree).toMatchSnapshot();
  });
});
