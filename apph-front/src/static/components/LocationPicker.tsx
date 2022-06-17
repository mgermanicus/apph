import { Autocomplete, TextField } from '@mui/material';
import { useState } from 'react';
import { ILocation } from '../../utils/types/Location';
import { LocationService } from '../../services/LocationService';
import { debounce } from 'ts-debounce';
import { useTranslation } from 'react-i18next';

type Props = {
  onChange: (value: ILocation) => void;
};
export const LocationPicker = ({ onChange }: Props): JSX.Element => {
  const { t, i18n } = useTranslation();
  const [suggestions, setSuggestions] = useState<ILocation[]>([]);
  const getSuggestions = debounce(
    (query: string) =>
      LocationService.geocode(
        query,
        i18n.language,
        setSuggestions,
        (errorMessage) => console.log(errorMessage)
      ),
    500
  );

  const handleChange = (value: ILocation) => {
    onChange(value);
  };

  return (
    <Autocomplete
      id="address"
      disableClearable
      options={suggestions}
      getOptionLabel={(suggestion) => suggestion?.address ?? ''}
      onChange={(event, value) => handleChange(value)}
      renderInput={(params) => (
        <TextField
          {...params}
          label={t('upload.address')}
          InputProps={{
            ...params.InputProps,
            type: 'search',
            onKeyUp: (event) => {
              const value = (event.target as HTMLInputElement).value;
              if (value.length > 2) {
                getSuggestions(value);
              }
            }
          }}
        />
      )}
    />
  );
};
