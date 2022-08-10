import { SendPhotosButton } from '../../static/components/SendPhotosButton';
import { render } from '@testing-library/react';
import { clickButton, fillText, triggerRequestSuccess } from '../utils';
import { screen } from '@testing-library/dom';

jest.mock('react-i18next', () => ({
  // this mock makes sure any components using the translate hook can use it without a warning being shown
  useTranslation: () => {
    return {
      t: (str: string) => str
    };
  }
}));

describe('Send Photo Button', () => {
  it('should show noneSelected photo', () => {
    //GIVEN
    render(<SendPhotosButton ids={[]} />);
    //WHEN
    clickButton(/send-photos/);
    //THEN
    expect(screen.getByText('photo.noneSelected')).toBeInTheDocument();
  });

  it('should render with no error', async () => {
    //GIVEN
    render(<SendPhotosButton ids={[1, 2]} />);
    triggerRequestSuccess('{ "message": "Success" }');
    clickButton(/send-photos/);
    fillText(/email.to/, 'test@viseo.com');
    fillText(/email.content/, 'content');
    fillText(/email.subject/, 'subject');
    //WHEN
    clickButton(/action.send/);
    //THEN
    expect(screen.getByText('Success')).toBeInTheDocument();
  });

  it('should show invalid email error', () => {
    //GIVEN
    render(<SendPhotosButton ids={[1, 2]} />);
    triggerRequestSuccess('{ "message": "Success" }');
    clickButton(/send-photos/);
    fillText(/email.to/, 'test@viseo');
    fillText(/email.content/, 'content');
    fillText(/email.subject/, 'subject');
    //WHEN
    clickButton(/action.send/);
    //THEN
    expect(screen.getByText('signup.error.email')).toBeInTheDocument();
  });

  it('should send folder', () => {
    //GIVEN
    render(<SendPhotosButton ids={[1]} isFolder={true} />);
    triggerRequestSuccess('{ "message": "Success" }');
    clickButton(/send-photos/);
    fillText(/email.to/, 'test@viseo.com');
    fillText(/email.content/, 'content');
    fillText(/email.subject/, 'subject');
    //WHEN
    clickButton(/action.send/);
    //THEN
    expect(screen.getByText('Success')).toBeInTheDocument();
  });
});
