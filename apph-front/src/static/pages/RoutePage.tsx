import { Header } from '../components/Header';

export const RoutePage = ({
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
