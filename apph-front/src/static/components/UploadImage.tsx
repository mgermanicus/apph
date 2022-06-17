import {
  Avatar,
  Box,
  Button,
  Container,
  CssBaseline,
  Dialog,
  Stack,
  TextField,
  Tooltip,
  Typography
} from '@mui/material';
import { AdapterDateFns } from '@mui/x-date-pickers/AdapterDateFns';
import PhotoService from '../../services/PhotoService';
import { ITag, StatusType, UploadStatus } from '../../utils';
import React, {
  Dispatch,
  FormEvent,
  SetStateAction,
  useEffect,
  useState
} from 'react';
import PhotoCamera from '@mui/icons-material/PhotoCamera';
import TagService from '../../services/TagService';
import { DesktopDatePicker, LocalizationProvider } from '@mui/lab';
import { useDispatch, useSelector } from 'react-redux';
import { setTagList } from '../../redux/slices/tagSlice';
import { UploadList } from './UploadList';
import { Upload } from '@mui/icons-material';
import { useTranslation } from 'react-i18next';
import { AlertSnackbar } from './AlertSnackbar';
import { TagInput } from './TagInput';
import { useDropzone } from 'react-dropzone';

export const UploadImage = ({
  setRefresh
}: {
  setRefresh?: Dispatch<SetStateAction<boolean>>;
}): JSX.Element => {
  const dispatch = useDispatch();
  const [title, setTitle] = useState<string>('');
  const [description, setDescription] = useState<string>('');
  const [shootingDate, setShootingDate] = useState<Date>(new Date());
  const [open, setOpen] = useState<boolean>(false);
  const [files, setFiles] = useState<File[]>();
  const [globalUploadStatus, setGlobalUploadStatus] = useState<UploadStatus>({
    type: StatusType.None
  });
  const { t } = useTranslation();
  const [uploadStatuses, setUploadStatuses] = useState<UploadStatus[]>([]);
  const [selectedTags, setSelectedTags] = useState<ITag[]>([]);
  const tagList = useSelector(({ tagList }: { tagList: ITag[] }) => tagList);
  const [tagsValidity, setTagsValidity] = useState<boolean>(true);
  const { acceptedFiles, getRootProps, getInputProps } = useDropzone({
    accept: { 'image/*': [] }
  });

  const createUploadCallbacks = (nbFiles: number) => {
    const handleSuccess = [];
    const handleError = [];
    for (let i = 0; i < nbFiles; i++) {
      handleSuccess.push(() => {
        setUploadStatuses((statuses) => [
          ...statuses.slice(0, i),
          {
            type: StatusType.Success
          },
          ...statuses.slice(i + 1)
        ]);
      });
      handleError.push((errorMessage: string) => {
        setUploadStatuses((statuses) => [
          ...statuses.slice(0, i),
          {
            type: StatusType.Error,
            message: errorMessage
          },
          ...statuses.slice(i + 1)
        ]);
      });
    }
    return { handleSuccess, handleError };
  };

  const handleClickOpen = () => {
    setOpen(true);
  };

  const handleClose = () => {
    setTitle('');
    setDescription('');
    setShootingDate(new Date());
    setSelectedTags([]);
    setUploadStatuses([]);
    setFiles(undefined);
    setGlobalUploadStatus({ type: StatusType.None });
    setOpen(false);
    if (setRefresh) {
      setRefresh((refresh) => !refresh);
    }
    acceptedFiles.length = 0;
  };

  const handleSubmit = (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    if (selectedTags.length < 1) {
      setTagsValidity(false);
      return;
    }
    if (acceptedFiles.length) {
      setFiles(acceptedFiles);
      setGlobalUploadStatus({ type: StatusType.Uploading });
      setUploadStatuses(
        Array(acceptedFiles.length).fill({ type: StatusType.Uploading })
      );
      const { handleSuccess, handleError } = createUploadCallbacks(
        acceptedFiles.length
      );

      // We wait for the first upload to finish before sending the others because if new tags are created we need
      // their id in the following requests
      PhotoService.uploadImage(
        acceptedFiles.length > 1 ? `${title}_1` : title,
        description,
        shootingDate,
        acceptedFiles[0],
        selectedTags,
        '-1',
        handleSuccess[0],
        handleError[0]
      )?.then(() => {
        TagService.getAllTags(
          (tags: string) => {
            const tagsConverted: ITag[] = JSON.parse(tags);
            dispatch(setTagList(tagsConverted));
            // Get the id of tags that have been created
            const newSelectedTags = selectedTags.map(
              (selectedTag) =>
                tagsConverted.find(
                  (tag) => `+ ${tag.name}` == selectedTag.name
                ) ?? selectedTag
            );
            setSelectedTags(newSelectedTags);
            for (let i = 1; i < acceptedFiles.length; i++) {
              PhotoService.uploadImage(
                `${title}_${i + 1}`,
                description,
                shootingDate,
                acceptedFiles[i],
                newSelectedTags,
                '-1',
                handleSuccess[i],
                handleError[i]
              );
            }
          },
          (errorMessage: string) =>
            setGlobalUploadStatus({
              type: StatusType.Error,
              message: errorMessage
            })
        );
      });
    } else {
      setGlobalUploadStatus({
        type: StatusType.Error,
        message: t('upload.error.addImage')
      });
    }
  };

  const updateGlobalStatus = () => {
    if (
      !uploadStatuses.length ||
      uploadStatuses.some(
        (status) =>
          status.type === StatusType.None ||
          status.type === StatusType.Uploading
      )
    )
      return;
    if (uploadStatuses.some((status) => status.type === StatusType.Error)) {
      setGlobalUploadStatus({
        type: StatusType.Error,
        message: t('upload.error.manyUploads')
      });
      return;
    }
    setGlobalUploadStatus({
      type: StatusType.Success,
      message: t('upload.manyUploads')
    });
    // Just enough time to see the success message
    setTimeout(handleClose, 1000);
  };

  useEffect(() => {
    getTagList();
  }, []);

  useEffect(() => {
    updateGlobalStatus();
  }, [uploadStatuses]);

  const getTagList = () => {
    TagService.getAllTags(
      (tags: string) => {
        const tagsConverted: ITag[] = JSON.parse(tags);
        dispatch(setTagList(tagsConverted));
      },
      (errorMessage: string) =>
        setGlobalUploadStatus({
          type: StatusType.Error,
          message: errorMessage
        })
    );
  };

  return (
    <Box sx={{ m: 1 }}>
      <Tooltip title={t('action.upload')}>
        <Button
          variant="outlined"
          onClick={handleClickOpen}
          aria-label="upload-photo"
        >
          <Upload />
        </Button>
      </Tooltip>
      <Dialog open={open} onClose={handleClose} data-testid="upload-dialog">
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
                {t('photo.add')}
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
                    label={t('photo.title')}
                    name="title"
                    autoComplete="title"
                    autoFocus
                    inputProps={{ maxLength: 255 }}
                  />
                  <TextField
                    required
                    fullWidth
                    value={description}
                    onChange={(event) => setDescription(event.target.value)}
                    id="description"
                    label={t('photoTable.description')}
                    name="description"
                    autoComplete="description"
                    autoFocus
                    inputProps={{ maxLength: 255 }}
                  />
                  <LocalizationProvider dateAdapter={AdapterDateFns}>
                    <DesktopDatePicker
                      label={t('photoTable.shootingDate')}
                      value={shootingDate}
                      onChange={(date) => {
                        if (date) setShootingDate(date);
                        else setShootingDate(new Date());
                      }}
                      renderInput={(params) => <TextField {...params} />}
                    />
                  </LocalizationProvider>
                  <TagInput
                    allTags={tagList}
                    onChange={(tags) => setSelectedTags(tags)}
                    isValid={tagsValidity}
                  />
                  <Box
                    {...getRootProps()}
                    sx={{
                      borderWidth: '2px',
                      borderStyle: 'dashed',
                      borderColor: '#d0d0d0',
                      backgroundColor: '#e8e8e8',
                      color: '#bdbdbd',
                      p: '20px',
                      alignItems: 'center'
                    }}
                  >
                    <input data-testid="drop-input" {...getInputProps()} />
                    <p>{t('upload.selectFile')}</p>
                  </Box>
                  {!!acceptedFiles.length && (
                    <aside>
                      <h4>{t('upload.files')}</h4>
                      <ul>
                        {acceptedFiles.map((file: File) => (
                          <li key={file.name}>{file.name}</li>
                        ))}
                      </ul>
                    </aside>
                  )}
                  <UploadList statuses={uploadStatuses} files={files} />
                  <Button
                    type="submit"
                    fullWidth
                    variant="contained"
                    disabled={
                      globalUploadStatus.type === StatusType.Uploading ||
                      globalUploadStatus.type === StatusType.Success
                    }
                  >
                    {t('action.add')}
                  </Button>
                </Stack>
                <AlertSnackbar
                  open={!!globalUploadStatus.message}
                  severity={
                    globalUploadStatus.type == StatusType.Error
                      ? 'error'
                      : 'success'
                  }
                  message={t(globalUploadStatus.message ?? '')}
                  onClose={() =>
                    setGlobalUploadStatus((status) => {
                      return { type: status.type, message: '' };
                    })
                  }
                />
              </Box>
            </Box>
          </CssBaseline>
        </Container>
      </Dialog>
    </Box>
  );
};
