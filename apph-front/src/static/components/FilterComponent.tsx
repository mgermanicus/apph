import {
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
import { ChangeEvent, useState } from 'react';
import { RemoveCircle } from '@mui/icons-material';
import { AdapterDateFns } from '@mui/x-date-pickers/AdapterDateFns';
import { DatePicker, LocalizationProvider } from '@mui/x-date-pickers';
import { Autocomplete } from '@mui/material';
import { useSelector } from 'react-redux';
import { ITag } from '../../utils';

const selectFieldStyle = {
  minWidth: '6vw',
  mr: '2vw'
};

const selectOperatorStyle = {
  minWidth: '8vw',
  mr: '2vw'
};

interface filterComponentProps {
  filterId: number;
  removeFilter: (filterId: number) => void;
}

export const FilterComponent = ({
  filterId,
  removeFilter
}: filterComponentProps) => {
  const [field, setField] = useState('');
  const [operator, setOperator] = useState('');
  const [value, setValue] = useState<string | Date | ITag[]>('');
  useState<string>('');
  const tagList = useSelector(({ tagList }: { tagList: ITag[] }) => tagList);

  const handleChangeField = (event: SelectChangeEvent) => {
    setField(event.target.value);
    setOperator('');
    setValue('');
  };

  const handleChangeOperator = (event: SelectChangeEvent) => {
    setOperator(event.target.value);
  };

  const getFields = () => (
    <Select
      labelId="select-field-label"
      id="select-field"
      value={field}
      onChange={handleChangeField}
      sx={selectFieldStyle}
    >
      <MenuItem value={'title'}>Titre</MenuItem>
      <MenuItem value={'description'}>Description</MenuItem>
      <MenuItem value={'creationDate'}>Date de création</MenuItem>
      <MenuItem value={'shootingDate'}>Date de prise de vue</MenuItem>
      <MenuItem value={'size'}>Taille(Ko)</MenuItem>
      <MenuItem value={'tags'}>Tags</MenuItem>
    </Select>
  );

  const getOperators = () => {
    switch (field) {
      case 'title':
      case 'description':
        return (
          <Select
            labelId="select-operator-label"
            id="select-operator"
            value={operator}
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
            value={operator}
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
    if (field === 'tags') {
      if (Array.isArray(value)) {
        return (
          <Autocomplete
            multiple
            id="fill-value-tags"
            size="small"
            options={tagList}
            getOptionLabel={(tag) => tag.name}
            value={value as ITag[]}
            onChange={(event, newValue) => {
              setValue(newValue);
            }}
            sx={{ minWidth: 200 }}
            renderInput={(params) => (
              <TextField {...params} label="Valeur" placeholder="Tags" />
            )}
          />
        );
      } else {
        setValue([]);
      }
    }
    switch (operator) {
      case 'is':
      case 'contain':
        return (
          <TextField
            id="fill-value-text"
            label="Valeur"
            multiline
            size="small"
            value={value}
            onChange={(event: ChangeEvent<HTMLInputElement>) => {
              setValue(event.target.value);
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
              value={value || new Date()}
              onChange={(value) => {
                if (value) setValue(value);
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
        <IconButton onClick={() => removeFilter(filterId)}>
          <RemoveCircle />
        </IconButton>
      </Tooltip>
    </Box>
  );
};
