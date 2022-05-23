import {
  Autocomplete,
  Box,
  FormControl,
  IconButton,
  InputLabel,
  MenuItem,
  Select,
  SelectChangeEvent,
  TextField,
  Tooltip
} from '@mui/material';
import * as React from 'react';
import { ChangeEvent, Dispatch } from 'react';
import { RemoveCircle } from '@mui/icons-material';
import { AdapterDateFns } from '@mui/x-date-pickers/AdapterDateFns';
import { DatePicker, LocalizationProvider } from '@mui/x-date-pickers';
import { ITag } from '../../utils';
import { IFilter } from '../../utils/types/Filter';
import { filterActionKind, filterActions } from './FilterSelector';

const selectFieldStyle = {
  minWidth: '6vw',
  mr: '2vw'
};

const selectOperatorStyle = {
  minWidth: '8vw',
  mr: '2vw'
};

interface filterComponentProps {
  state: IFilter;
  dispatchFilterState: Dispatch<filterActions>;
  tagList: ITag[];
}

export const FilterComponent = ({
  state,
  dispatchFilterState,
  tagList
}: filterComponentProps) => {
  const handleChangeField = (event: SelectChangeEvent) => {
    dispatchFilterState({
      type: filterActionKind.UPDATE,
      payload: {
        id: state.id,
        field: event.target.value,
        operator: '',
        value: ''
      }
    });
  };

  const handleChangeOperator = (event: SelectChangeEvent | null) => {
    dispatchFilterState({
      type: filterActionKind.UPDATE,
      payload: {
        id: state.id,
        field: state.field,
        operator: event?.target.value || null,
        value: state.value
      }
    });
  };

  const handleChangeValue = (value: string | Date | ITag[]) => {
    dispatchFilterState({
      type: filterActionKind.UPDATE,
      payload: {
        id: state.id,
        field: state.field,
        operator: state.operator,
        value: value
      }
    });
  };

  const getFields = () => (
    <Select
      labelId="select-field-label"
      id="select-field"
      value={state.field}
      onChange={handleChangeField}
      sx={selectFieldStyle}
    >
      <MenuItem value={'title'}>Titre</MenuItem>
      <MenuItem value={'description'}>Description</MenuItem>
      <MenuItem value={'creationDate'}>Date de création</MenuItem>
      <MenuItem value={'shootingDate'}>Date de prise de vue</MenuItem>
      <MenuItem value={'tags'}>Tags</MenuItem>
    </Select>
  );

  const getOperators = () => {
    switch (state.field) {
      case 'title':
      case 'description':
        return (
          <Select
            labelId="select-operator-label"
            id="select-operator"
            value={state.operator || ''}
            onChange={handleChangeOperator}
            sx={selectOperatorStyle}
          >
            <MenuItem value={'is'}>EGAL</MenuItem>
            <MenuItem value={'contain'}>CONTIENT</MenuItem>
          </Select>
        );
      case 'creationDate':
      case 'shootingDate':
        return (
          <Select
            labelId="select-operator-label"
            id="select-operator"
            value={state.operator || ''}
            onChange={handleChangeOperator}
            sx={selectOperatorStyle}
          >
            <MenuItem value={'strictlySuperior'}>{'>'}</MenuItem>
            <MenuItem value={'strictlyInferior'}>{'<'}</MenuItem>
            <MenuItem value={'superiorEqual'}>{'>='}</MenuItem>
            <MenuItem value={'inferiorEqual'}>{'<='}</MenuItem>
            <MenuItem value={'equal'}>{'='}</MenuItem>
          </Select>
        );
      case 'tags':
        return null;
      default:
        return (
          <Select
            labelId="select-operator-label"
            id="select-operator"
            sx={selectOperatorStyle}
            disabled
            value=""
          />
        );
    }
  };

  const getInputValue = () => {
    if (state.field === 'tags') {
      if (Array.isArray(state.value)) {
        return (
          <Autocomplete
            multiple
            id="fill-value-tags"
            size="small"
            options={tagList}
            getOptionLabel={(tag) => tag.name}
            value={state.value as ITag[]}
            onChange={(event, newValue) => {
              handleChangeValue(newValue);
            }}
            sx={{ minWidth: 200 }}
            renderInput={(params) => (
              <TextField {...params} label="Valeur" placeholder="Tags" />
            )}
          />
        );
      } else {
        handleChangeValue([]);
      }
    }
    switch (state.operator) {
      case 'is':
      case 'contain':
        return (
          <TextField
            id="fill-value-text"
            label="Valeur"
            multiline
            size="small"
            value={state.value}
            onChange={(event: ChangeEvent<HTMLInputElement>) => {
              handleChangeValue(event.target.value);
            }}
            type="text"
          />
        );
      case 'strictlySuperior':
      case 'strictlyInferior':
      case 'superiorEqual':
      case 'inferiorEqual':
      case 'equal':
        return (
          <LocalizationProvider dateAdapter={AdapterDateFns}>
            <DatePicker
              label="Valeur"
              value={state.value || new Date()}
              onChange={(value) => {
                if (value) handleChangeValue(value);
              }}
              renderInput={(params) => (
                <TextField {...params} id="fill-value-date" size="small" />
              )}
            />
          </LocalizationProvider>
        );
      default:
        return (
          <TextField id="fill-value" label="Valeur" size="small" disabled />
        );
    }
  };
  return (
    <Box>
      <FormControl size="small">
        <InputLabel id="select-field-label">Champ</InputLabel>
        {getFields()}
      </FormControl>
      <FormControl size="small">
        <InputLabel id="select-operator-label">Opérateur</InputLabel>
        {getOperators()}
      </FormControl>
      <FormControl>{getInputValue()}</FormControl>
      <Tooltip title="Supprimer un filtre" arrow>
        <IconButton
          onClick={() =>
            dispatchFilterState({
              type: filterActionKind.REMOVE,
              payload: state.id
            })
          }
        >
          <RemoveCircle />
        </IconButton>
      </Tooltip>
    </Box>
  );
};
