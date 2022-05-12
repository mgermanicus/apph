import { TODOPage } from './TODOPage';
import { PhotoTable } from '../components/PhotoTable';

export const AdvancedResearchPage = (): JSX.Element => {
  const getFilteredPhotos = () => {
    return null;
  };

  return (
    <>
      <TODOPage todo={'filterSelector'} />
      <PhotoTable getPhotos={getFilteredPhotos} />
    </>
  );
};
