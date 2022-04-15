import { render } from '@testing-library/react';
import { Header } from '../../static/components/Header';
import { BrowserRouter } from 'react-router-dom';
import { HeaderContainer } from '../../static/containers/HeaderContainer';

describe('Header', () => {
  test('Should render without crash', async () => {
    render(
      <BrowserRouter>
        <HeaderContainer />
      </BrowserRouter>
    );
  });
});
