import { Autocomplete, TextField } from '@mui/material';
import { createRef, useState } from 'react';
import { ILocation } from '../../utils/types/Location';
import { LocationService } from '../../services/LocationService';
import { debounce } from 'ts-debounce';
import { useTranslation } from 'react-i18next';

type Props = {
  onChange: (value: ILocation) => void;
  isValid: boolean;
  defaultValue?: ILocation;
};
export const LocationPicker = ({
  onChange,
  isValid,
  defaultValue
}: Props): JSX.Element => {
  const { t, i18n } = useTranslation();
  const [selectedLocation, setSelectedLocation] = useState<ILocation>(
    defaultValue ?? { address: '', position: { lat: 0, lng: 0 } }
  );
  const [suggestions, setSuggestions] = useState<ILocation[]>([]);
  const locationInput = createRef<HTMLInputElement>();

  if (!isValid) locationInput.current?.setCustomValidity('upload.fillField');
  else locationInput.current?.setCustomValidity('');
  const getSuggestions = debounce(
    (query: string) =>
      LocationService.geocode(
        query,
        i18n.language,
        (locations) => {
          setSuggestions(locations);
        },
        (errorMessage) => console.log(errorMessage)
      ),
    500
  );

  const handleChange = (value: ILocation) => {
    setSelectedLocation(value);
    onChange(value);
  };

  return (
    <Autocomplete
      id="address"
      data-testid="location"
      disableClearable
      options={suggestions}
      getOptionLabel={(suggestion) => suggestion?.address ?? ''}
      onChange={(event, value) => {
        handleChange(value);
      }}
      defaultValue={defaultValue}
      renderInput={(params) => (
        <TextField
          required
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
            },
            required: !selectedLocation
          }}
          inputRef={locationInput}
        />
      )}
    />
  );
};
