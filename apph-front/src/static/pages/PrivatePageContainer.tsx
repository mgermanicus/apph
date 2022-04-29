import { Header } from '../components/Header';

export const PrivatePageContainer = ({
  element
}: {
  element: JSX.Element;
}): JSX.Element => {
  return (
    <>
      <Header />
      {element}
    </>
  );
};
