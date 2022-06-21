import { Box, Button, Dialog, Stack, TextField, Tooltip } from '@mui/material';
import EditIcon from '@mui/icons-material/Edit';
import React, {
  Dispatch,
  FormEvent,
  SetStateAction,
  useEffect,
  useState
} from 'react';
import { ITag } from '../../utils';
import { DesktopDatePicker, LocalizationProvider } from '@mui/lab';
import { AdapterDateFns } from '@mui/x-date-pickers/AdapterDateFns';
import { TagInput } from './TagInput';
import { useTranslation } from 'react-i18next';
import TagService from '../../services/TagService';
import { AlertSnackbar } from './AlertSnackbar';
import Typography from '@mui/material/Typography';
import PhotoService from '../../services/PhotoService';

interface modifyPhotosProps {
  ids: number[];
  setRefresh: Dispatch<SetStateAction<boolean>>;
}

export const ModifyPhotos = ({
  ids,
  setRefresh
}: modifyPhotosProps): JSX.Element => {
  const [isFormOpen, setIsFormOpen] = useState<boolean>(false);
  const [shootingDate, setShootingDate] = useState<Date>(new Date());
  const [selectedTags, setSelectedTags] = useState<ITag[]>([]);
  const [allTags, setAllTags] = useState<ITag[]>([]);
  const [tagsValidity, setTagsValidity] = useState<boolean>(true);
  const [errorMessage, setErrorMessage] = useState<string>('');
  const { t } = useTranslation();

  useEffect(() => {
    TagService.getAllTags(
      (tags: string) => {
        const tagsConverted: ITag[] = JSON.parse(tags);
        setAllTags(tagsConverted);
      },
      (errorMessage: string) => {
        setErrorMessage(errorMessage);
      }
    );
  }, []);

  const handleOpenForm = () => {
    setIsFormOpen(true);
  };

  const handleCloseForm = () => {
    setShootingDate(new Date());
    setSelectedTags([]);
    setIsFormOpen(false);
  };

  const handleSubmit = (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    if (selectedTags.length < 1) {
      setTagsValidity(false);
      return;
    }
    PhotoService.editPhotoListInfos(
      ids,
      shootingDate,
      selectedTags,
      () => {
        setRefresh((refresh) => !refresh);
        handleCloseForm();
      },
      (errorMessage: string) => setErrorMessage(errorMessage)
    );
  };

  return (
    <>
      <Box sx={{ m: 1 }}>
        <Tooltip title={t('photo.modifyDetails')}>
          <Button
            variant="outlined"
            onClick={handleOpenForm}
            aria-label="upload-photo"
          >
            <EditIcon />
          </Button>
        </Tooltip>
      </Box>
      <Dialog
        open={isFormOpen}
        onClose={handleCloseForm}
        aria-labelledby="modal-modal-title"
      >
        <Box
          component="form"
          onSubmit={handleSubmit}
          sx={{
            m: 3,
            display: 'flex',
            flexDirection: 'column',
            alignItems: 'center'
          }}
        >
          <Typography
            id="modal-modal-title"
            component="h1"
            align="center"
            sx={{ fontSize: '2rem' }}
          >
            {t('photo.modifyDetailsTitle')}
          </Typography>
          <Stack
            direction="column"
            spacing={2}
            sx={{
              width: {
                xs: 200,
                sm: 300,
                lg: 400,
                xl: 400
              },
              m: 2
            }}
          >
            <LocalizationProvider dateAdapter={AdapterDateFns}>
              <DesktopDatePicker
                label={t('photoTable.shootingDate')}
                value={shootingDate}
                onChange={(date) => {
                  if (date) setShootingDate(date);
                  else setShootingDate(new Date());
                }}
                renderInput={(params) => <TextField {...params} required />}
              />
            </LocalizationProvider>
            <TagInput
              allTags={allTags}
              onChange={(tags) => setSelectedTags(tags)}
              isValid={tagsValidity}
            />
            <Button type="submit" fullWidth variant="contained">
              {t('action.confirm')}
            </Button>
          </Stack>
        </Box>
        <AlertSnackbar
          open={!!errorMessage}
          severity={'warning'}
          message={errorMessage}
          onClose={() => setErrorMessage('')}
        />
      </Dialog>
    </>
  );
};
