import { FilterSelector } from '../components/FilterSelector';
import { PhotoTable } from '../components/PhotoTable';
import { useState } from 'react';

export const AdvancedResearchPage = (): JSX.Element => {
  const [showTable, setShowTable] = useState(false);

  const getFilteredPhotos = () => {
    setShowTable(true);
    console.log('Test');
  };

  return (
    <>
      <FilterSelector onFilterPhoto={getFilteredPhotos} />
      {showTable && <PhotoTable getPhotos={getFilteredPhotos} />}
    </>
  );
};
