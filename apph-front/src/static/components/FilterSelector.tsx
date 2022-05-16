import * as React from 'react';
import { AddCircle } from '@mui/icons-material';
import { FilterComponent } from './FilterComponent';
import { Box, IconButton, Tooltip } from '@mui/material';
import { useEffect, useState } from 'react';
import TagService from '../../services/TagService';
import { ITag } from '../../utils';
import { setTagList } from '../../redux/slices/tagSlice';
import { useDispatch } from 'react-redux';

const filterSelectorBoxStyle = {
  display: 'flex',
  flexDirection: 'column',
  alignItems: 'center',
  '> div, button': { mt: '1vh' }
};

export const FilterSelector = () => {
  const dispatch = useDispatch();
  const removeFilter = (filterId: number) => {
    setFilters((prevState) => {
      return prevState.filter((filter) => filter.key != filterId);
    });
  };
  const [filters, setFilters] = useState([
    <FilterComponent key={0} filterId={0} removeFilter={removeFilter} />
  ]);
  const [dynamicKey, setDynamicKey] = useState(1);

  const addFilter = () => {
    setFilters([
      ...filters,
      <FilterComponent
        key={dynamicKey}
        filterId={dynamicKey}
        removeFilter={removeFilter}
      />
    ]);
    setDynamicKey(dynamicKey + 1);
  };

  useEffect(() => {
    TagService.getAllTags(
      (tags: string) => {
        const tagsConverted: ITag[] = JSON.parse(tags);
        dispatch(setTagList(tagsConverted));
      },
      (errorMessage: string) => console.log(errorMessage)
    );
  }, []);

  return (
    <Box sx={filterSelectorBoxStyle}>
      {filters}
      <Tooltip title="Ajouter un filtre" arrow>
        <IconButton onClick={addFilter}>
          <AddCircle />
        </IconButton>
      </Tooltip>
    </Box>
  );
};
