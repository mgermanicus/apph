import {
  AlertColor,
  Box,
  Button,
  Dialog,
  Stack,
  TextField,
  Tooltip
} from '@mui/material';
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
import moment from 'moment';
import i18n from 'i18next';

interface modifyPhotosProps {
  ids: number[];
  setRefresh?: Dispatch<SetStateAction<boolean>>;
}

export const ModifyPhotos = ({
  ids,
  setRefresh
}: modifyPhotosProps): JSX.Element => {
  const [isFormOpen, setIsFormOpen] = useState<boolean>(false);
  const [shootingDate, setShootingDate] = useState<string>();
  const [selectedTags, setSelectedTags] = useState<ITag[] | undefined>();
  const [allTags, setAllTags] = useState<ITag[]>([]);
  const [errorMessage, setErrorMessage] = useState<string>('');
  const [snackBarSeverity, setSnackBarSeverity] =
    useState<AlertColor>('warning');
  const { t } = useTranslation();

  useEffect(() => {
    TagService.getAllTags(
      (tags: string) => {
        const tagsConverted: ITag[] = JSON.parse(tags);
        setAllTags(tagsConverted);
      },
      (errorMessage: string) => {
        openSnackBar('error', errorMessage);
      }
    );
  }, []);

  const openSnackBar = (severity: AlertColor, message: string) => {
    setSnackBarSeverity(severity);
    setErrorMessage(message);
  };

  const handleOpenForm = () => {
    if (ids.length != 0) {
      setIsFormOpen(true);
    } else {
      openSnackBar('warning', 'photo.noneSelected');
    }
  };

  const handleCloseForm = () => {
    setShootingDate(undefined);
    setSelectedTags(undefined);
    setIsFormOpen(false);
  };

  const handleSubmit = (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    if (!shootingDate && !selectedTags) {
      openSnackBar('error', 'photo.error.requireOneField');
      return;
    }
    PhotoService.editPhotoListInfos(
      ids,
      (message: string) => {
        if (setRefresh) setRefresh((refresh) => !refresh);
        openSnackBar('success', message);
        handleCloseForm();
      },
      (errorMessage: string) => {
        openSnackBar('error', errorMessage);
      },
      shootingDate,
      selectedTags
    );
  };

  return (
    <>
      <Box sx={{ m: 1 }}>
        <Tooltip title={t('photo.modifyDetails')}>
          <Button
            variant="outlined"
            onClick={handleOpenForm}
            aria-label="modify-photos"
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
                value={shootingDate || null}
                onChange={(date) => {
                  if (date) {
                    setShootingDate(moment(date).format('MM/DD/YYYY'));
                  } else setShootingDate(moment().format('MM/DD/YYYY'));
                }}
                inputFormat={
                  i18n.language == 'fr' ? 'dd/MM/yyyy' : 'MM/dd/yyyy'
                }
                renderInput={(params) => <TextField {...params} />}
              />
            </LocalizationProvider>
            <TagInput
              allTags={allTags}
              onChange={(tags) => setSelectedTags(tags)}
              required={false}
            />
            <Button type="submit" fullWidth variant="contained">
              {t('action.confirm')}
            </Button>
          </Stack>
        </Box>
      </Dialog>
      <AlertSnackbar
        open={!!errorMessage}
        severity={snackBarSeverity}
        message={errorMessage}
        onClose={() => setErrorMessage('')}
      />
    </>
  );
};
