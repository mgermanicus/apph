import { render } from '@testing-library/react';
import { ModifyPhotos } from '../../static/components/ModifyPhotos';
import { clickButton, fillDate, fillTags } from '../utils';
import PhotoService from '../../services/PhotoService';
import userEvent from '@testing-library/user-event';
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

function setup(element: JSX.Element) {
  return {
    user: userEvent.setup(),
    ...render(element, { wrapper })
  };
}

describe('ModifyPhotos component tests', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('tests when an user edit shooting date and tags', () => {
    //GIVEN
    jest.spyOn(PhotoService, 'editPhotoListInfos');
    const modifications = {
      shootingDate: new Date(),
      tags: [{ name: 'new tag' }]
    };
    setup(<ModifyPhotos ids={[0]} />);
    //WHEN
    clickButton(/modify-photos/);
    fillDate(modifications.shootingDate);
    fillTags(modifications.tags);
    clickButton(/action.confirm/);
    //THEN
    expect(PhotoService.editPhotoListInfos).toBeCalledWith(
      [0],
      expect.anything(),
      expect.anything(),
      expect.anything(),
      undefined,
      undefined
    );
  });

  it('tests when form is empty and submitted', () => {
    //GIVEN
    jest.spyOn(PhotoService, 'editPhotoListInfos');
    jest.mock('../../static/components/AlertSnackbar.tsx');
    const wrapper = setup(<ModifyPhotos ids={[0]} />);
    //WHEN
    clickButton(/modify-photos/);
    clickButton(/action.confirm/);
    //THEN
    expect(PhotoService.editPhotoListInfos).not.toBeCalled();
    expect(
      wrapper.getByText(/photo.error.requireOneField/)
    ).toBeInTheDocument();
  });
});
