import { render } from '@testing-library/react';
import { BrowserRouter } from 'react-router-dom';
import { UserProfileContainer } from '../../static/containers/UserProfileContainer';

describe('User Profile Container', () => {
  test('Should render without crash', async () => {
    render(
      <BrowserRouter>
        <UserProfileContainer />
      </BrowserRouter>
    );
  });
});
