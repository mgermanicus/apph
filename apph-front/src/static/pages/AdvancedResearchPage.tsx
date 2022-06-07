import * as React from 'react';
import { useState } from 'react';
import { FilterSelector } from '../components/FilterSelector';
import { IFilter, IFilterPayload } from '../../utils/types/Filter';
import { AlertSnackbar } from '../components/AlertSnackbar';
import { usePhotoTable } from '../../utils/hooks/usePhotoTable';
import { deepCopy } from '../../utils/DeepCopy';

export const AdvancedResearchPage = (): JSX.Element => {
  const [filterList, setFilterList] = useState<IFilterPayload[]>();

  const { errorState, photoTable } = usePhotoTable(filterList);

  const getFilterPayload = (filterList: IFilter[]): void => {
    if (
      filterList.length === 0 ||
      filterList.some((filterState) =>
        Object.values(filterState).some(
          (value) =>
            value === undefined ||
            value === '' ||
            (Array.isArray(value) && value.length === 0)
        )
      )
    ) {
      errorState.setMessage('Au moins un filtre possède un champ vide.');
    } else {
      const filterPayload = filterList as IFilterPayload[];
      setFilterList(deserializeArray(filterPayload));
    }
  };

  const deserializeArray = (
    filterPayload: IFilterPayload[]
  ): IFilterPayload[] => {
    filterPayload.forEach((filter) => {
      delete filter.id;
      if (Array.isArray(filter.value)) {
        const oldTagsArray = filter.value;
        filter.value = filter.value[0].name;
        for (let i = 1; i < oldTagsArray.length; i++) {
          const newFilter = {
            field: filter.field,
            operator: null,
            value: oldTagsArray[i].name
          };
          filterPayload.push(newFilter);
        }
      }
    });
    return filterPayload;
  };

  const handleFilterPhoto = (filterList: IFilter[]) => {
    const filters: IFilter[] = deepCopy(filterList);
    getFilterPayload(filters);
  };

  return (
    <>
      <FilterSelector
        onFilterPhoto={handleFilterPhoto}
        onError={errorState.setMessage}
      />
      {filterList?.length && photoTable}
      <AlertSnackbar
        open={!!errorState.getMessage}
        severity={'warning'}
        message={errorState.getMessage}
        onClose={() => errorState.setMessage('')}
      />
    </>
  );
};
