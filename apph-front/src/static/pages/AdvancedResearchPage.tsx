import * as React from 'react';
import { useEffect, useState } from 'react';
import { FilterSelector } from '../components/FilterSelector';
import { IFilter, IFilterPayload } from '../../utils/types/Filter';
import { AlertSnackbar } from '../components/AlertSnackbar';
import { usePhotoTable } from '../../utils/hooks/usePhotoTable';
import { deepCopy } from '../../utils/DeepCopy';
import { useLocation } from 'react-router-dom';
import TagService from '../../services/TagService';
import { ITag } from '../../utils';
import { setTagList } from '../../redux/slices/tagSlice';
import { useDispatch } from 'react-redux';

export const AdvancedResearchPage = (): JSX.Element => {
  const location = useLocation();
  const dispatch = useDispatch();
  const [filterList, setFilterList] = useState<IFilterPayload[]>([]);
  const { errorState, photoTable } = usePhotoTable(filterList);

  useEffect(() => {
    TagService.getAllTags(
      (tags: string) => {
        const tagsConverted: ITag[] = JSON.parse(tags);
        dispatch(setTagList(tagsConverted));
      },
      (errorMessage: string) => errorState.setMessage(errorMessage)
    );
  }, []);

  const getFilterPayload = (filterList: IFilter[]): void => {
    if (
      filterList.length === 0 ||
      (filterList.some((filterState) =>
        Object.values(filterState).some(
          (value) =>
            value === undefined ||
            value === '' ||
            (Array.isArray(value) && value.length === 0)
        )
      ) &&
        location.state === null)
    ) {
      errorState.setMessage('filter.error.fieldNull');
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
        tagName={(location?.state as { tagName: string })?.tagName}
        onFilterPhoto={handleFilterPhoto}
        onError={errorState.setMessage}
      />
      {!!filterList?.length && photoTable}
      <AlertSnackbar
        open={!!errorState.getMessage}
        severity={'warning'}
        message={errorState.getMessage}
        onClose={() => errorState.setMessage('')}
      />
    </>
  );
};
