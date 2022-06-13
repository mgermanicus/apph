import * as React from 'react';
import { useEffect, useReducer } from 'react';
import { AddCircle } from '@mui/icons-material';
import { FilterComponent } from './FilterComponent';
import { Box, IconButton, Tooltip } from '@mui/material';
import SearchIcon from '@mui/icons-material/Search';
import TagService from '../../services/TagService';
import { ITag } from '../../utils';
import { setTagList } from '../../redux/slices/tagSlice';
import { useDispatch, useSelector } from 'react-redux';
import { IFilter } from '../../utils/types/Filter';
import { useTranslation } from 'react-i18next';

const filterSelectorBoxStyle = {
  display: 'flex',
  flexDirection: 'column',
  alignItems: 'center',
  '> div, button': { mt: '1vh' }
};

const filterButtonStyle = { mb: '2vh', mx: '1vh' };

const isInstanceOfIFilter = (object: object): object is IFilter => {
  return (
    'id' in object &&
    ('field' in object || 'operator' in object || 'value' in object)
  );
};

export enum filterActionKind {
  ADD = 'addFilter',
  REMOVE = 'removeFilter',
  UPDATE = 'updateFilter'
}

export interface filterActions {
  type: filterActionKind;
  payload: IFilter | number;
}

const filterInitialState: IFilter[] = [
  {
    id: 0,
    field: '',
    operator: '',
    value: ''
  }
];

function* generateId() {
  let id = 1;
  while (true) yield id++;
}
const iterator = generateId();

interface filterSelectorProps {
  onFilterPhoto: (filterList: IFilter[]) => void;
  onError: (message: string) => void;
}

export const FilterSelector = ({
  onFilterPhoto,
  onError
}: filterSelectorProps) => {
  const filterReducers = (state: IFilter[], action: filterActions) => {
    const { type, payload } = action;
    switch (type) {
      case filterActionKind.ADD:
        if (typeof payload == 'number')
          return [
            ...state,
            {
              id: payload,
              field: '',
              operator: '',
              value: ''
            }
          ];
        throw new Error(t('filter.error.addPayloadNaN'));
      case filterActionKind.REMOVE:
        if (typeof payload == 'number')
          return state.filter((filter) => filter.id != action.payload);
        throw new Error(t('filter.error.removePayloadNaN'));
      case filterActionKind.UPDATE:
        if (typeof payload == 'object' && isInstanceOfIFilter(payload)) {
          const filterIndex = state.findIndex(
            (filter) => filter.id == payload.id
          );
          state[filterIndex] = {
            id: state[filterIndex].id,
            field:
              payload.field !== 'undefined'
                ? payload.field
                : state[filterIndex].field,
            operator:
              payload.field === 'tags'
                ? null
                : payload.operator !== 'undefined'
                ? payload.operator
                : state[filterIndex].operator,
            value:
              payload.value !== 'undefined'
                ? payload.value
                : state[filterIndex].value
          };
          return [...state];
        }
        throw new Error('filter.error.idNotMatch');
      default:
        throw new Error('filter.error.gestion');
    }
  };
  const [filterStates, dispatchFilterStates] = useReducer(
    filterReducers,
    filterInitialState
  );
  const tagList = useSelector(({ tagList }: { tagList: ITag[] }) => tagList);
  const dispatch = useDispatch();
  const { t } = useTranslation();

  useEffect(() => {
    TagService.getAllTags(
      (tags: string) => {
        const tagsConverted: ITag[] = JSON.parse(tags);
        dispatch(setTagList(tagsConverted));
      },
      (errorMessage: string) => onError(errorMessage)
    );
  }, []);

  const createNewFilter = () => {
    const currentIterator = iterator.next();
    if (!currentIterator.done)
      dispatchFilterStates({
        type: filterActionKind.ADD,
        payload: currentIterator.value
      });
  };

  const handleFilterPhoto = () => {
    onFilterPhoto(filterStates);
  };

  const getFilterList = () =>
    filterStates.map((filterState) => (
      <FilterComponent
        key={filterState.id}
        state={filterState}
        dispatchFilterState={dispatchFilterStates}
        tagList={tagList}
      />
    ));

  return (
    <Box sx={filterSelectorBoxStyle}>
      {getFilterList()}
      <Box>
        <Tooltip title={t('filter.add')} arrow>
          <IconButton onClick={createNewFilter} sx={filterButtonStyle}>
            <AddCircle />
          </IconButton>
        </Tooltip>
        <Tooltip title={t('action.search')} arrow>
          <IconButton
            color="primary"
            onClick={handleFilterPhoto}
            sx={filterButtonStyle}
          >
            <SearchIcon />
          </IconButton>
        </Tooltip>
      </Box>
    </Box>
  );
};
