import {
  Alert,
  Autocomplete,
  Avatar,
  Box,
  Button,
  Container,
  createFilterOptions,
  CssBaseline,
  Dialog,
  FilterOptionsState,
  Input,
  LinearProgress,
  Stack,
  TextField,
  Typography
} from '@mui/material';
import { AdapterDateFns } from '@mui/x-date-pickers/AdapterDateFns';
import PhotoService from '../../services/PhotoService';
import { ITag, UploadStatus } from '../../utils';
import { createRef, FormEvent, useEffect, useState } from 'react';
import PhotoCamera from '@mui/icons-material/PhotoCamera';
import TagService from '../../services/TagService';
import { DesktopDatePicker, LocalizationProvider } from '@mui/lab';
import { useDispatch, useSelector } from 'react-redux';
import { setTagList } from '../../redux/slices/tagSlice';

const filter = createFilterOptions<ITag>();

const displayAlert = (
  uploadStatus: UploadStatus,
  errorMessage = "Une erreur est survenue lors de l'upload"
) => {
  switch (uploadStatus) {
    case 'success':
      return <Alert severity="success">Votre fichier a bien été uploadé</Alert>;
    case 'error':
      return <Alert severity="error">{errorMessage}</Alert>;
    default:
      return <></>;
  }
};

export const UploadImage = (): JSX.Element => {
  const dispatch = useDispatch();
  const [title, setTitle] = useState<string>('');
  const [description, setDescription] = useState<string>('');
  const [shootingDate, setShootingDate] = useState<Date>(new Date());
  const [uploadStatus, setUploadStatus] = useState<UploadStatus>('none');
  const [errorMessage, setErrorMessage] = useState<string>('');
  const fileInput = createRef<HTMLInputElement>();
  const [open, setOpen] = useState<boolean>(false);
  const [selectedTags, setSelectedTags] = useState<ITag[]>([]);
  const tagList = useSelector(({ tagList }: { tagList: ITag[] }) => tagList);
  const handleClickOpen = () => {
    setOpen(true);
  };

  const handleClose = () => {
    setOpen(false);
    setErrorMessage('');
    setTitle('');
    setDescription('');
    setShootingDate(new Date());
    setSelectedTags([]);
    setUploadStatus('none');
  };

  const handleSubmit = (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    const files = fileInput.current?.files;
    if (files) {
      const file = files[0];
      setUploadStatus('uploading');
      PhotoService.uploadImage(
        title,
        description,
        shootingDate,
        file,
        selectedTags,
        '-1',
        () => {
          getTagList();
          setUploadStatus('success');
          handleClose();
        },
        (errorMessage) => {
          setUploadStatus('error');
          setErrorMessage(errorMessage);
        }
      );
    }
  };

  const filterTags = (options: ITag[], params: FilterOptionsState<ITag>) => {
    const filtered = filter(
      options?.filter((tag) => tag.name !== null),
      params
    );
    const { inputValue } = params;
    const isExisting = options.some((option) => inputValue === option.name);
    if (inputValue !== '' && !isExisting) {
      filtered.push({
        name: `+ Add New Tag ${inputValue}`
      });
    }
    return filtered;
  };

  useEffect(() => {
    getTagList();
  }, []);

  const getTagList = () => {
    TagService.getAllTags(
      (tags: string) => {
        const tagsConverted: ITag[] = JSON.parse(tags);
        dispatch(setTagList(tagsConverted));
      },
      (errorMessage: string) => setErrorMessage(errorMessage)
    );
  };

  return (
    <Box sx={{ m: 1 }}>
      <Button variant="outlined" onClick={handleClickOpen}>
        Upload
      </Button>
      <Dialog open={open} onClose={handleClose}>
        <Container component="main">
          <CssBaseline>
            <Box
              sx={{
                display: 'flex',
                flexDirection: 'column',
                alignItems: 'center',
                mb: 3
              }}
            >
              <Avatar sx={{ m: 1, bgcolor: 'secondary.main' }}>
                <PhotoCamera />
              </Avatar>
              <Typography component="h1" variant="h5">
                Ajouter une photo
              </Typography>
              <Box component="form" onSubmit={handleSubmit} sx={{ mt: 1 }}>
                <Stack
                  direction="column"
                  spacing={2}
                  sx={{
                    width: {
                      xs: 200,
                      sm: 300,
                      lg: 400,
                      xl: 500
                    }
                  }}
                >
                  <TextField
                    required
                    fullWidth
                    value={title}
                    onChange={(event) => setTitle(event.target.value)}
                    id="title"
                    label="Titre de la photo"
                    name="title"
                    autoComplete="title"
                    autoFocus
                  />
                  <TextField
                    required
                    fullWidth
                    value={description}
                    onChange={(event) => setDescription(event.target.value)}
                    id="description"
                    label="Description"
                    name="description"
                    autoComplete="description"
                    autoFocus
                  />
                  <LocalizationProvider dateAdapter={AdapterDateFns}>
                    <DesktopDatePicker
                      label="Date de prise en vue"
                      value={shootingDate}
                      onChange={(date) => {
                        if (date) setShootingDate(date);
                        else setShootingDate(new Date());
                      }}
                      renderInput={(params) => <TextField {...params} />}
                    />
                  </LocalizationProvider>
                  <Autocomplete
                    data-testid="#autocomplete"
                    multiple
                    limitTags={2}
                    id="tags"
                    size="small"
                    options={tagList}
                    onChange={(event, tags) => setSelectedTags(tags)}
                    filterOptions={(options, params) =>
                      filterTags(options, params)
                    }
                    isOptionEqualToValue={(tag, value) =>
                      tag.name === value.name
                    }
                    getOptionLabel={(tag) => tag.name}
                    renderInput={(params) => (
                      <TextField
                        required
                        {...params}
                        inputProps={{
                          ...params.inputProps,
                          autoComplete: 'new-password',
                          required: selectedTags.length === 0
                        }}
                        label="Tags"
                      />
                    )}
                  />
                  <Input
                    fullWidth
                    inputRef={fileInput}
                    inputProps={{
                      type: 'file',
                      accept: 'image/*',
                      'data-testid': 'file-input'
                    }}
                    required
                  />
                  {uploadStatus === 'uploading' && <LinearProgress />}
                  <Button
                    type="submit"
                    fullWidth
                    variant="contained"
                    disabled={uploadStatus === 'uploading'}
                  >
                    Ajouter
                  </Button>
                  {displayAlert(uploadStatus, errorMessage)}
                </Stack>
              </Box>
            </Box>
          </CssBaseline>
        </Container>
      </Dialog>
    </Box>
  );
};
