import {
  Autocomplete,
  createFilterOptions,
  FilterOptionsState,
  TextField
} from '@mui/material';
import { createRef, useState } from 'react';
import { ITag } from '../../utils';

type Props = {
  allTags: ITag[];
  onChange: (selectedTags: ITag[]) => void;
  required: boolean;
  isValid?: boolean;
  defaultValue?: ITag[];
};

export const TagInput = ({
  allTags,
  onChange,
  required,
  isValid,
  defaultValue
}: Props) => {
  const [selectedTags, setSelectedTags] = useState<ITag[]>(defaultValue ?? []);
  const filter = createFilterOptions<ITag>();
  const tagsInput = createRef<HTMLInputElement>();

  if (!isValid) tagsInput.current?.setCustomValidity('upload.fillField');
  else tagsInput.current?.setCustomValidity('');

  const filterTags = (options: ITag[], params: FilterOptionsState<ITag>) => {
    const filtered = filter(
      options?.filter((tag) => tag.name !== null),
      params
    );
    const { inputValue } = params;
    const isExisting = options.some((option) => inputValue === option.name);
    if (inputValue !== '' && !isExisting) {
      filtered.push({
        name: `+ ${inputValue}`
      });
    }
    return filtered;
  };

  const handleChange = (tags: ITag[]) => {
    setSelectedTags(tags);
    onChange(tags);
  };

  return (
    <Autocomplete
      data-testid="tags"
      multiple
      limitTags={2}
      id="tags"
      size="small"
      options={allTags}
      onChange={(event, tags) => handleChange(tags)}
      filterOptions={(options, params) => filterTags(options, params)}
      defaultValue={defaultValue}
      isOptionEqualToValue={(tag, value) => tag.name === value.name}
      getOptionLabel={(tag) => tag.name}
      renderInput={(params) => (
        <TextField
          required={required}
          {...params}
          inputProps={{
            ...params.inputProps,
            autoComplete: 'new-password',
            required: required ? selectedTags.length === 0 : false
          }}
          inputRef={tagsInput}
          label="Tags"
        />
      )}
    />
  );
};
