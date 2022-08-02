import { render } from '@testing-library/react';
import { AppSettingsPage } from '../../static/pages/AppSettingsPage';
import { clickButton, fakeRequest, fillText } from '../utils';
import { wrapper } from '../utils/components/CustomWrapper';

jest.mock('react-i18next', () => ({
  // this mock makes sure any components using the translate hook can use it without a warning being shown
  useTranslation: () => {
    return {
      t: (str: string) => str
    };
  }
}));

describe('AppSettingsPage', () => {
  it('should render', async () => {
    //GIVEN
    const spyRequestFunction = fakeRequest({
      '/user/getSettings': { body: '{"uploadSize":1,"downloadSize":2}' },
      '/admin/updateSettings': { body: 'success' }
    });
    //WHEN
    const tree = render(<AppSettingsPage />, { wrapper });
    fillText(/admin.setting.uploadSize/, '10');
    fillText(/admin.setting.downloadSize/, '20');
    clickButton(/action.confirm/);
    clickButton(/action.continue/);
    //THEN
    expect(tree).toMatchSnapshot();
    expect(spyRequestFunction).toBeCalledWith(
      '/admin/updateSettings',
      expect.anything(),
      expect.anything(),
      expect.anything()
    );
  });
});
