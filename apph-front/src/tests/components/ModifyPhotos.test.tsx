import { ModifyPhotos } from '../../static/components/ModifyPhotos';
import { clickButton, fillDate, fillTags } from '../utils';
import PhotoService from '../../services/PhotoService';
import { renderWithWrapper } from '../utils';
import { screen } from '@testing-library/dom';

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
    renderWithWrapper(<ModifyPhotos ids={[0]} />);
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
    renderWithWrapper(<ModifyPhotos ids={[0]} />);
    //WHEN
    clickButton(/modify-photos/);
    clickButton(/action.confirm/);
    //THEN
    expect(PhotoService.editPhotoListInfos).not.toBeCalled();
    expect(screen.getByText(/photo.error.requireOneField/)).toBeInTheDocument();
  });
});
