import { render } from '@testing-library/react';
import { Header } from '../../static/components/Header';
import { BrowserRouter } from 'react-router-dom';

describe('Header', () => {
  test('Should render without crash', async () => {
    render(
      <BrowserRouter>
        <Header isAuth={true} />
      </BrowserRouter>
    );
  });
});
