import { FilterSelector } from '../components/FilterSelector';
import { PhotoTable } from '../components/PhotoTable';

export const AdvancedResearchPage = (): JSX.Element => {
  const getFilteredPhotos = () => {
    return null;
  };

  return (
    <>
      <FilterSelector />
      {/*<PhotoTable getPhotos={getFilteredPhotos} />*/}
    </>
  );
};
