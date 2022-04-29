import { Store } from '@reduxjs/toolkit';
import { Provider } from 'react-redux';
import { MemoryRouter } from 'react-router-dom';
import store from '../../../redux/store/store';

const ReduxProvider = ({
  children,
  reduxStore
}: {
  children: JSX.Element;
  reduxStore: Store;
}) => <Provider store={reduxStore}>{children}</Provider>;

const wrapperRedux = ({ children }: { children: JSX.Element }) => (
  <MemoryRouter>
    <ReduxProvider reduxStore={store}>{children}</ReduxProvider>
  </MemoryRouter>
);

export { wrapperRedux as wrapper };
